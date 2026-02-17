package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.OwnedCoupon;
import io.dodn.commerce.core.enums.CouponType;
import io.dodn.commerce.core.enums.OwnedCouponState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class OwnedCouponResponse {
    private final Long id;
    private final OwnedCouponState state;
    private final String name;
    private final CouponType type;
    private final BigDecimal discount;
    private final LocalDateTime expiredAt;

    public static OwnedCouponResponse of(OwnedCoupon ownedCoupon) {
        return new OwnedCouponResponse(
                ownedCoupon.getId(),
                ownedCoupon.getState(),
                ownedCoupon.getCoupon().getName(),
                ownedCoupon.getCoupon().getType(),
                ownedCoupon.getCoupon().getDiscount(),
                ownedCoupon.getCoupon().getExpiredAt()
        );
    }

    public static List<OwnedCouponResponse> of(List<OwnedCoupon> ownedCoupons) {
        return ownedCoupons.stream().map(OwnedCouponResponse::of).collect(Collectors.toList());
    }
}
