package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OwnedCouponState;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.CouponRepository;
import io.dodn.commerce.storage.db.core.OwnedCouponEntity;
import io.dodn.commerce.storage.db.core.OwnedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnedCouponService {

    private final CouponRepository couponRepository;
    private final OwnedCouponRepository ownedCouponRepository;

    public List<OwnedCoupon> getOwnedCoupons(User user) {
        var ownedCoupons = ownedCouponRepository.findByUserIdAndStatus(user.getId(), EntityStatus.ACTIVE);
        if (ownedCoupons.isEmpty()) return Collections.emptyList();

        var couponMap = couponRepository.findAllById(
                        ownedCoupons.stream().map(it -> it.getCouponId()).collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(it -> it.getId(), Function.identity()));

        return ownedCoupons.stream()
                .map(it -> {
                    var coupon = couponMap.get(it.getCouponId());
                    return new OwnedCoupon(
                            it.getId(),
                            it.getUserId(),
                            it.getState(),
                            new Coupon(coupon.getId(), coupon.getName(), coupon.getType(), coupon.getDiscount(), coupon.getExpiredAt())
                    );
                })
                .collect(Collectors.toList());
    }

    public void download(User user, Long couponId) {
        var coupon = couponRepository.findByIdAndStatusAndExpiredAtAfter(couponId, EntityStatus.ACTIVE, LocalDateTime.now());
        if (coupon == null) throw new CoreException(ErrorType.COUPON_NOT_FOUND_OR_EXPIRED);

        var existing = ownedCouponRepository.findByUserIdAndCouponId(user.getId(), couponId);
        if (existing != null) {
            throw new CoreException(ErrorType.COUPON_ALREADY_DOWNLOADED);
        }
        ownedCouponRepository.save(new OwnedCouponEntity(
                user.getId(),
                coupon.getId(),
                OwnedCouponState.DOWNLOADED
        ));
    }

    public List<OwnedCoupon> getOwnedCouponsForCheckout(User user, Collection<Long> productIds) {
        if (productIds.isEmpty()) return Collections.emptyList();

        var applicableCouponMap = couponRepository.findApplicableCouponIds(productIds).stream()
                .collect(Collectors.toMap(it -> it.getId(), Function.identity()));
        if (applicableCouponMap.isEmpty()) return Collections.emptyList();

        var ownedCoupons = ownedCouponRepository.findOwnedCouponIds(user.getId(), applicableCouponMap.keySet(), LocalDateTime.now());
        if (ownedCoupons.isEmpty()) return Collections.emptyList();

        return ownedCoupons.stream()
                .map(it -> {
                    var coupon = applicableCouponMap.get(it.getCouponId());
                    return new OwnedCoupon(
                            it.getId(),
                            it.getUserId(),
                            it.getState(),
                            new Coupon(coupon.getId(), coupon.getName(), coupon.getType(), coupon.getDiscount(), coupon.getExpiredAt())
                    );
                })
                .collect(Collectors.toList());
    }
}
