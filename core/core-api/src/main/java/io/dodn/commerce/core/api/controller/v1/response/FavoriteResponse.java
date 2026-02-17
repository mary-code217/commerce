package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.Favorite;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class FavoriteResponse {
    private final Long id;
    private final Long productId;
    private final LocalDateTime favoritedAt;

    public static FavoriteResponse of(Favorite f) {
        return new FavoriteResponse(
                f.getId(),
                f.getProductId(),
                f.getFavoritedAt()
        );
    }

    public static List<FavoriteResponse> of(List<Favorite> favorites) {
        return favorites.stream().map(FavoriteResponse::of).collect(Collectors.toList());
    }
}
