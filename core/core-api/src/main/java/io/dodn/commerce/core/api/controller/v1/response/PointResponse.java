package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.PointBalance;
import io.dodn.commerce.core.domain.PointHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PointResponse {
    private final Long userId;
    private final BigDecimal balance;
    private final List<PointHistoryResponse> histories;

    public static PointResponse of(PointBalance balance, List<PointHistory> histories) {
        return new PointResponse(
                balance.getUserId(),
                balance.getBalance(),
                histories.stream().map(it -> new PointHistoryResponse(
                        it.getType(),
                        it.getAmount(),
                        it.getAppliedAt()
                )).collect(Collectors.toList())
        );
    }
}
