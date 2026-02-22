package io.dodn.commerce.storage.db.core;

import io.dodn.commerce.core.enums.TransactionType;
import io.dodn.commerce.storage.db.CoreDbContextTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SettlementTargetRepositoryTest extends CoreDbContextTest {

    private final SettlementTargetRepository settlementTargetRepository;

    SettlementTargetRepositoryTest(SettlementTargetRepository settlementTargetRepository) {
        this.settlementTargetRepository = settlementTargetRepository;
    }

    @Test
    void 정산일자별_가맹점_집계가_정상적으로_조회_되어야한다() {
        // given
        LocalDate date = LocalDate.now().minusMonths(1);

        // 가맹점 1: 2개의 행, 주문 ID 중복으로 DISTINCT 주문 수 집계 검증
        settlementTargetRepository.save(
                new SettlementTargetEntity(
                        1L, date, new BigDecimal(1000), TransactionType.PAYMENT,
                        10L, 100L, 1000L, 1L, new BigDecimal(1000), new BigDecimal(1000)
                )
        );
        settlementTargetRepository.save(
                new SettlementTargetEntity(
                        1L, date, new BigDecimal(2000), TransactionType.PAYMENT,
                        11L, 100L, 1001L, 2L, new BigDecimal(1000), new BigDecimal(2000)
                )
        );

        // 가맹점 2: 동일 정산일에 1개의 행
        settlementTargetRepository.save(
                new SettlementTargetEntity(
                        2L, date, new BigDecimal(3000), TransactionType.PAYMENT,
                        12L, 200L, 2000L, 3L, new BigDecimal(1000), new BigDecimal(3000)
                )
        );

        // 다른 정산일 데이터는 제외되어야 함
        settlementTargetRepository.save(
                new SettlementTargetEntity(
                        1L, date.minusDays(1), new BigDecimal(9999), TransactionType.PAYMENT,
                        13L, 300L, 3000L, 9L, new BigDecimal(1111), new BigDecimal(9999)
                )
        );

        // when
        List<SettlementTargetSummary> summaries = settlementTargetRepository.findSummary(date);

        // then
        assertThat(summaries).hasSize(2);

        Map<Long, SettlementTargetSummary> byMerchant = summaries.stream()
                .collect(Collectors.toMap(SettlementTargetSummary::getMerchantId, s -> s));

        SettlementTargetSummary m1 = byMerchant.get(1L);
        assertThat(m1.getSettlementDate()).isEqualTo(date);
        assertThat(m1.getTargetAmount()).isEqualByComparingTo(new BigDecimal(3000));
        assertThat(m1.getTargetCount()).isEqualTo(2);
        assertThat(m1.getOrderCount()).isEqualTo(1);

        SettlementTargetSummary m2 = byMerchant.get(2L);
        assertThat(m2.getSettlementDate()).isEqualTo(date);
        assertThat(m2.getTargetAmount()).isEqualByComparingTo(new BigDecimal(3000));
        assertThat(m2.getTargetCount()).isEqualTo(1);
        assertThat(m2.getOrderCount()).isEqualTo(1);
    }

    @Test
    void 정산일자별_가맹점_집계가_취소를_포함하여도_정상적으로_조회_되어야한다() {
        // given
        LocalDate date = LocalDate.now();

        // 가맹점 1: 결제 2건 + 취소 1건(음수)
        settlementTargetRepository.save(
                new SettlementTargetEntity(
                        1L, date, new BigDecimal(1500), TransactionType.PAYMENT,
                        101L, 1000L, 9000L, 1L, new BigDecimal(1500), new BigDecimal(1500)
                )
        );
        settlementTargetRepository.save(
                new SettlementTargetEntity(
                        1L, date, new BigDecimal(500), TransactionType.PAYMENT,
                        102L, 1001L, 9001L, 1L, new BigDecimal(500), new BigDecimal(500)
                )
        );
        settlementTargetRepository.save(
                new SettlementTargetEntity(
                        1L, date, new BigDecimal(-500), TransactionType.CANCEL,
                        103L, 1001L, 9001L, 1L, new BigDecimal(500), new BigDecimal(500)
                )
        );

        // 가맹점 2: 결제 1건, 취소 1건(음수) -> 순합 0, 주문 2개(DISTINCT)
        settlementTargetRepository.save(
                new SettlementTargetEntity(
                        2L, date, new BigDecimal(700), TransactionType.PAYMENT,
                        201L, 2000L, 8000L, 1L, new BigDecimal(700), new BigDecimal(700)
                )
        );
        settlementTargetRepository.save(
                new SettlementTargetEntity(
                        2L, date, new BigDecimal(-700), TransactionType.CANCEL,
                        202L, 2001L, 8001L, 1L, new BigDecimal(700), new BigDecimal(700)
                )
        );

        // when
        List<SettlementTargetSummary> summaries = settlementTargetRepository.findSummary(date);

        // then
        assertThat(summaries).hasSize(2);
        Map<Long, SettlementTargetSummary> byMerchant = summaries.stream()
                .collect(Collectors.toMap(SettlementTargetSummary::getMerchantId, s -> s));

        SettlementTargetSummary m1 = byMerchant.get(1L);
        assertThat(m1.getTargetAmount()).isEqualByComparingTo(new BigDecimal(1500));
        assertThat(m1.getTargetCount()).isEqualTo(3);
        assertThat(m1.getOrderCount()).isEqualTo(2);

        SettlementTargetSummary m2 = byMerchant.get(2L);
        assertThat(m2.getTargetAmount()).isEqualByComparingTo(new BigDecimal(0));
        assertThat(m2.getTargetCount()).isEqualTo(2);
        assertThat(m2.getOrderCount()).isEqualTo(2);
    }
}
