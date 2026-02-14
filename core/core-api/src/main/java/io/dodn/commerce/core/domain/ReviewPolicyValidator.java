package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.core.enums.ReviewTargetType;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.OrderItemRepository;
import io.dodn.commerce.storage.db.core.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewPolicyValidator {

    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    public ReviewKey validateNew(User user, ReviewTarget target) {
        if (target.getType() == ReviewTargetType.PRODUCT) {
            List<String> reviewKeys = orderItemRepository.findRecentOrderItemsForProduct(
                            user.getId(), target.getId(), OrderState.PAID,
                            LocalDateTime.now().minusDays(14), EntityStatus.ACTIVE
                    ).stream()
                    .map(it -> "ORDER_ITEM_" + it.getId())
                    .collect(Collectors.toList());

            Set<String> existReviewKeys = reviewRepository.findByUserIdAndReviewKeyIn(user.getId(), reviewKeys).stream()
                    .map(it -> it.getReviewKey())
                    .collect(Collectors.toSet());

            String availableKey = reviewKeys.stream()
                    .filter(key -> !existReviewKeys.contains(key))
                    .findFirst()
                    .orElseThrow(() -> new CoreException(ErrorType.REVIEW_HAS_NOT_ORDER));

            return new ReviewKey(user, availableKey);
        }
        throw new UnsupportedOperationException();
    }

    public void validateUpdate(User user, Long reviewId) {
        var review = reviewRepository.findByIdAndUserId(reviewId, user.getId());
        if (review == null) throw new CoreException(ErrorType.NOT_FOUND_DATA);
        if (review.getCreatedAt().plusDays(7).isBefore(LocalDateTime.now())) {
            throw new CoreException(ErrorType.REVIEW_UPDATE_EXPIRED);
        }
    }
}
