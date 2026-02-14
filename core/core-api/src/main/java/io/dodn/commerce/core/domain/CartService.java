package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.CartItemEntity;
import io.dodn.commerce.storage.db.core.CartItemRepository;
import io.dodn.commerce.storage.db.core.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public Cart getCart(User user) {
        var items = cartItemRepository.findByUserIdAndStatus(user.getId(), EntityStatus.ACTIVE);
        Map<Long, io.dodn.commerce.storage.db.core.ProductEntity> productMap =
                productRepository.findAllById(items.stream().map(it -> it.getProductId()).collect(Collectors.toList()))
                        .stream()
                        .collect(Collectors.toMap(it -> it.getId(), Function.identity()));

        return new Cart(
                user.getId(),
                items.stream()
                        .filter(it -> productMap.containsKey(it.getProductId()))
                        .map(it -> {
                            var product = productMap.get(it.getProductId());
                            return new CartItem(
                                    it.getId(),
                                    new Product(
                                            product.getId(),
                                            product.getName(),
                                            product.getThumbnailUrl(),
                                            product.getDescription(),
                                            product.getShortDescription(),
                                            new Price(product.getCostPrice(), product.getSalesPrice(), product.getDiscountedPrice())
                                    ),
                                    it.getQuantity()
                            );
                        })
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public Long addCartItem(User user, AddCartItem item) {
        var existing = cartItemRepository.findByUserIdAndProductId(user.getId(), item.getProductId());
        if (existing != null) {
            if (existing.isDeleted()) {
                existing.active();
            }
            existing.applyQuantity(item.getQuantity());
            return existing.getId();
        }
        return cartItemRepository.save(
                new CartItemEntity(user.getId(), item.getProductId(), item.getQuantity())
        ).getId();
    }

    @Transactional
    public Long modifyCartItem(User user, ModifyCartItem item) {
        var found = cartItemRepository.findByUserIdAndIdAndStatus(user.getId(), item.getCartItemId(), EntityStatus.ACTIVE);
        if (found == null) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }
        found.applyQuantity(item.getQuantity());
        return found.getId();
    }

    @Transactional
    public void deleteCartItem(User user, Long cartItemId) {
        var entity = cartItemRepository.findByUserIdAndIdAndStatus(user.getId(), cartItemId, EntityStatus.ACTIVE);
        if (entity == null) {
            throw new CoreException(ErrorType.NOT_FOUND_DATA);
        }
        entity.delete();
    }
}
