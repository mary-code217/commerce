package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import io.dodn.commerce.storage.db.core.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReviewFinder {

    private final ReviewRepository reviewRepository;

    public Page<Review> find(ReviewTarget target, OffsetLimit offsetLimit) {
        var result = reviewRepository.findByTargetTypeAndTargetIdAndStatus(
                target.getType(), target.getId(), EntityStatus.ACTIVE, offsetLimit.toPageable()
        );
        return new Page<>(
                result.getContent().stream()
                        .map(it -> new Review(
                                it.getId(),
                                it.getUserId(),
                                new ReviewTarget(it.getTargetType(), it.getTargetId()),
                                new ReviewContent(it.getRate(), it.getContent())
                        ))
                        .collect(Collectors.toList()),
                result.hasNext()
        );
    }

    public RateSummary findRateSummary(ReviewTarget target) {
        var founds = reviewRepository.findByTargetTypeAndTargetId(target.getType(), target.getId()).stream()
                .filter(it -> it.isActive())
                .collect(Collectors.toList());

        if (founds.isEmpty()) {
            return RateSummary.EMPTY;
        }
        BigDecimal totalRate = founds.stream()
                .map(it -> it.getRate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new RateSummary(
                totalRate.divide(BigDecimal.valueOf(founds.size())),
                (long) founds.size()
        );
    }
}
