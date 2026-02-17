package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.enums.PointType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PointHistoryResponse {
    private final PointType type;
    private final BigDecimal amount;
    private final LocalDateTime appliedAt;
}
