package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Coupon;
import io.dodn.commerce.core.enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CouponResponse {
    private final Long id;
    private final String name;
    private final CouponType type;
    private final BigDecimal discount;
    private final LocalDateTime expiredAt;

    public static CouponResponse of(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getType(),
                coupon.getDiscount(),
                coupon.getExpiredAt()
        );
    }

    public static List<CouponResponse> of(List<Coupon> coupons) {
        return coupons.stream().map(CouponResponse::of).collect(Collectors.toList());
    }
}
