package io.dodn.commerce.core.api.controller.batch;

import io.dodn.commerce.core.domain.SettlementService;
import io.dodn.commerce.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class SettlementBatchController {

    private final SettlementService settlementService;

    /**
     * NOTE: 정산 대상 적재 배치
     * - 오전 1시 실행
     * - 어제 00:00:00 ~ 23:59:59 데이터 기준으로 정산 데이터 적재
     */
    @PostMapping("/internal-batch/load-targets")
    public ApiResponse<Object> loadTargets(@RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate targetDate) {
        settlementService.loadTargets(
                targetDate,
                targetDate.minusDays(1).atStartOfDay(),
                targetDate.atStartOfDay().minusNanos(1)
        );
        return ApiResponse.success();
    }

    /**
     * NOTE: 정산 계산 배치
     * - 오전 4시 실행
     */
    @PostMapping("/internal-batch/calculate")
    public ApiResponse<Object> calculate(@RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate targetDate) {
        settlementService.calculate(targetDate);
        return ApiResponse.success();
    }

    /**
     * NOTE: 정산 입금 배치
     * - 오전 9시 실행
     */
    @PostMapping("/internal-batch/transfer")
    public ApiResponse<Object> transfer() {
        settlementService.transfer();
        return ApiResponse.success();
    }
}
