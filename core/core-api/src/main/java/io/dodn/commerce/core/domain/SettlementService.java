package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.PaymentState;
import io.dodn.commerce.core.enums.SettlementState;
import io.dodn.commerce.core.enums.TransactionType;
import io.dodn.commerce.storage.db.core.CancelRepository;
import io.dodn.commerce.storage.db.core.PaymentRepository;
import io.dodn.commerce.storage.db.core.SettlementEntity;
import io.dodn.commerce.storage.db.core.SettlementRepository;
import io.dodn.commerce.storage.db.core.SettlementTargetRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final PaymentRepository paymentRepository;
    private final CancelRepository cancelRepository;
    private final SettlementTargetRepository settlementTargetRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementTargetLoader settlementTargetLoader;

    public void loadTargets(LocalDate settleDate, LocalDateTime from, LocalDateTime to) {
        Pageable paymentPageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"));
        do {
            var payments = paymentRepository.findAllByStateAndPaidAtBetween(PaymentState.SUCCESS, from, to, paymentPageable);
            try {
                settlementTargetLoader.process(settleDate, TransactionType.PAYMENT,
                        payments.getContent().stream().collect(Collectors.toMap(it -> it.getOrderId(), it -> it.getId())));
            } catch (Exception e) {
                log.error("[SETTLEMENT_LOAD_TARGETS] `결제` 거래건 정산 대상 생성 중 오류 발생 offset: {} size: {} page: {} error: {}",
                        paymentPageable.getOffset(), paymentPageable.getPageSize(), paymentPageable.getPageNumber(), e.getMessage(), e);
            }
            paymentPageable = payments.nextPageable();
        } while (payments.hasNext());

        Pageable cancelPageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"));
        do {
            var cancels = cancelRepository.findAllByCanceledAtBetween(from, to, cancelPageable);
            try {
                settlementTargetLoader.process(settleDate, TransactionType.CANCEL,
                        cancels.getContent().stream().collect(Collectors.toMap(it -> it.getOrderId(), it -> it.getId())));
            } catch (Exception e) {
                log.error("[SETTLEMENT_LOAD_TARGETS] `취소` 거래건 정산 대상 생성 중 오류 발생 offset: {} size: {} page: {} error: {}",
                        cancelPageable.getOffset(), cancelPageable.getPageSize(), cancelPageable.getPageNumber(), e.getMessage(), e);
            }
            cancelPageable = cancels.nextPageable();
        } while (cancels.hasNext());
    }

    @Transactional
    public int calculate(LocalDate settleDate) {
        var summary = settlementTargetRepository.findSummary(settleDate);
        List<SettlementEntity> settlements = summary.stream()
                .map(it -> {
                    SettlementAmount amount = SettlementCalculator.calculate(it.getTargetAmount());
                    return new SettlementEntity(
                            it.getMerchantId(),
                            it.getSettlementDate(),
                            amount.getOriginalAmount(),
                            amount.getFeeAmount(),
                            amount.getFeeRate(),
                            amount.getSettlementAmount(),
                            SettlementState.READY
                    );
                })
                .collect(Collectors.toList());
        settlementRepository.saveAll(settlements);
        return settlements.size();
    }

    public int transfer() {
        Map<Long, List<SettlementEntity>> settlements = settlementRepository.findByState(SettlementState.READY).stream()
                .collect(Collectors.groupingBy(it -> it.getMerchantId()));

        for (var entry : settlements.entrySet()) {
            try {
                BigDecimal transferAmount = entry.getValue().stream()
                        .map(it -> it.getSettlementAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    // NOTE: 총 정산금이 음수라면 돈 보낼 필요가 없다는 것이므로, 정산금이 양수가 될 때까지 스킵
                    log.warn("[SETTLEMENT_TRANSFER] {} 가맹점 미정산 금액 : {} 발생 확인 요망!", entry.getKey(), transferAmount);
                    continue;
                }

                /**
                 * NOTE: 외부 펌 등 이체 서비스 API 호출
                 */

                entry.getValue().forEach(it -> it.sent());
                settlementRepository.saveAll(entry.getValue());
            } catch (Exception e) {
                log.error("[SETTLEMENT_TRANSFER] {} 가맹점 정산 중 에러 발생: {}", entry.getKey(), e.getMessage(), e);
            }
        }
        return settlements.size();
    }
}
