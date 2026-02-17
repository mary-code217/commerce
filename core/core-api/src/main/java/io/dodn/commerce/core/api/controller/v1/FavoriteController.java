package io.dodn.commerce.core.api.controller.v1;

import io.dodn.commerce.core.api.controller.v1.request.ApplyFavoriteRequest;
import io.dodn.commerce.core.api.controller.v1.request.ApplyFavoriteRequestType;
import io.dodn.commerce.core.api.controller.v1.response.FavoriteResponse;
import io.dodn.commerce.core.domain.FavoriteService;
import io.dodn.commerce.core.domain.User;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.response.ApiResponse;
import io.dodn.commerce.core.support.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/v1/favorites")
    public ApiResponse<PageResponse<FavoriteResponse>> getFavorites(
            User user,
            @RequestParam int offset,
            @RequestParam int limit
    ) {
        var page = favoriteService.findFavorites(user, new OffsetLimit(offset, limit));
        return ApiResponse.success(new PageResponse<>(FavoriteResponse.of(page.getContent()), page.isHasNext()));
    }

    @PostMapping("/v1/favorites")
    public ApiResponse<Object> applyFavorite(User user, @RequestBody ApplyFavoriteRequest request) {
        switch (request.getType()) {
            case FAVORITE -> favoriteService.addFavorite(user, request.getProductId());
            case UNFAVORITE -> favoriteService.removeFavorite(user, request.getProductId());
        }
        return ApiResponse.success();
    }
}
