package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {

    //Убрать EntityManager, т.к. используются репозитории
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public void createOrder(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product ids cannot be empty");
        }
        var orderItems = productIds.stream()
                .map(id -> OrderItemEntity.builder()
                        .productId(id)
                        .build())
                .collect(Collectors.toSet());
        createOrderFromItems(orderItems);
    }

    @Transactional
    public void createOrderFromItems(Set<OrderItemEntity> orderItems) {
        var order = OrderEntity.builder()
                .orderItems(orderItems)
                .build();
        orderRepository.save(order);  // Используйте репозиторий для сохранения
        publishOrderCreation(order);
    }

    @Transactional
    public List<Long> returnOrder(Long orderId, Long returnedProductId) {
        if (returnedProductId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.isIssued()) {
            throw new IllegalArgumentException("Order has already been issued");
        }
        var orderItem = order.getOrderItems().stream()
                .filter(item -> item.getProductId().equals(returnedProductId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in order"));

        if (orderItem.isReturned()) {
            throw new IllegalArgumentException("Product has already been returned");
        }
        orderItem.setReturned(true);
        publishOrderReturn(orderId, returnedProductId);
        return orderItemRepository.getNotReturnedProductIds(orderId);
    }

    @Transactional
    public void issueOrder(Long orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.isIssued()) {
            throw new IllegalArgumentException("Order has already been issued");
        }
        order.setIssued(true);
        orderRepository.save(order);  // Используйте репозиторий для сохранения
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()  // Замените parallelStream на stream
                .map(order -> {
                    var productIds = order.getOrderItems().stream()
                            .filter(orderItem -> !orderItem.isReturned())
                            .map(OrderItemEntity::getProductId)
                            .collect(Collectors.toList());
                    return new OrderDto(order.getId(), productIds);
                })
                .collect(Collectors.toList());
    }

    private void publishOrderCreation(OrderEntity order) {
        // Внутри метода происходит отправка данных о создании заказа в платежную систему
    }

    private void publishOrderReturn(long orderId, long productId) {
        // Внутри метода происходит запрос на возврат средств по товару в заказе
    }
}
