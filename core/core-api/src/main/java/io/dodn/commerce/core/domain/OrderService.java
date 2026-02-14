package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.enums.OrderState;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.OrderEntity;
import io.dodn.commerce.storage.db.core.OrderItemEntity;
import io.dodn.commerce.storage.db.core.OrderItemRepository;
import io.dodn.commerce.storage.db.core.OrderRepository;
import io.dodn.commerce.storage.db.core.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderKeyGenerator orderKeyGenerator;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public String create(User user, NewOrder newOrder) {
        Set<Long> orderProductIds = newOrder.getItems().stream()
                .map(NewOrderItem::getProductId)
                .collect(Collectors.toSet());

        Map<Long, io.dodn.commerce.storage.db.core.ProductEntity> productMap =
                productRepository.findByIdInAndStatus(orderProductIds, EntityStatus.ACTIVE).stream()
                        .collect(Collectors.toMap(it -> it.getId(), Function.identity()));

        if (productMap.isEmpty()) throw new CoreException(ErrorType.NOT_FOUND_DATA);
        if (!productMap.keySet().equals(orderProductIds)) throw new CoreException(ErrorType.PRODUCT_MISMATCH_IN_ORDER);

        NewOrderItem firstItem = newOrder.getItems().get(0);
        String orderName = productMap.get(firstItem.getProductId()).getName()
                + (newOrder.getItems().size() > 1 ? " 외 " + (newOrder.getItems().size() - 1) + "개" : "");

        BigDecimal totalPrice = newOrder.getItems().stream()
                .map(it -> productMap.get(it.getProductId()).getDiscountedPrice()
                        .multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity(
                user.getId(),
                orderKeyGenerator.generate(),
                orderName,
                totalPrice,
                OrderState.CREATED
        );
        OrderEntity savedOrder = orderRepository.save(order);

        List<OrderItemEntity> orderItems = newOrder.getItems().stream()
                .map(it -> {
                    var product = productMap.get(it.getProductId());
                    return new OrderItemEntity(
                            savedOrder.getId(),
                            product.getId(),
                            product.getName(),
                            product.getThumbnailUrl(),
                            product.getShortDescription(),
                            it.getQuantity(),
                            product.getDiscountedPrice(),
                            product.getDiscountedPrice().multiply(BigDecimal.valueOf(it.getQuantity()))
                    );
                })
                .collect(Collectors.toList());
        orderItemRepository.saveAll(orderItems);

        return savedOrder.getOrderKey();
    }

    @Transactional
    public List<OrderSummary> getOrders(User user) {
        var orders = orderRepository.findByUserIdAndStateAndStatusOrderByIdDesc(
                user.getId(), OrderState.PAID, EntityStatus.ACTIVE
        );
        if (orders.isEmpty()) return Collections.emptyList();

        return orders.stream()
                .map(it -> new OrderSummary(
                        it.getId(),
                        it.getOrderKey(),
                        it.getName(),
                        user.getId(),
                        it.getTotalPrice(),
                        it.getState()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Order getOrder(User user, String orderKey, OrderState state) {
        var order = orderRepository.findByOrderKeyAndStateAndStatus(orderKey, state, EntityStatus.ACTIVE);
        if (order == null) throw new CoreException(ErrorType.NOT_FOUND_DATA);
        if (!order.getUserId().equals(user.getId())) throw new CoreException(ErrorType.NOT_FOUND_DATA);

        var items = orderItemRepository.findByOrderId(order.getId());
        if (items.isEmpty()) throw new CoreException(ErrorType.NOT_FOUND_DATA);

        return new Order(
                order.getId(),
                order.getOrderKey(),
                order.getName(),
                user.getId(),
                order.getTotalPrice(),
                order.getState(),
                items.stream()
                        .map(it -> new OrderItem(
                                order.getId(),
                                it.getProductId(),
                                it.getProductName(),
                                it.getThumbnailUrl(),
                                it.getShortDescription(),
                                it.getQuantity(),
                                it.getUnitPrice(),
                                it.getTotalPrice()
                        ))
                        .collect(Collectors.toList())
        );
    }
}
