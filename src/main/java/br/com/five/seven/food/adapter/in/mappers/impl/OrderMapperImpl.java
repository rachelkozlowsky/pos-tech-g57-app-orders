package br.com.five.seven.food.adapter.in.mappers.impl;

import br.com.five.seven.food.adapter.in.mappers.ItemMapper;
import br.com.five.seven.food.adapter.in.mappers.OrderMapper;
import br.com.five.seven.food.adapter.in.payload.order.CreateOrderRequest;
import br.com.five.seven.food.adapter.in.payload.order.OrderMonitorResponse;
import br.com.five.seven.food.adapter.in.payload.order.OrderResponse;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderItemsRequest;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderRequest;
import br.com.five.seven.food.adapter.out.relational.entity.OrderEntity;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderMapperImpl implements OrderMapper {

    private final ItemMapper itemMapper;

    @Override
    public Order createRequestToDomain(CreateOrderRequest createOrderRequest) {
        Order order = new Order(
                null,
                createOrderRequest.getTitle(),
                createOrderRequest.getDescription(),
                OrderStatus.CREATED,
                createOrderRequest.getCpfClient(),
                null,  // Items set below to establish bidirectional relationship
                BigDecimal.ZERO,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Map items and establish bidirectional relationship
        var items = itemMapper.requestListToDomainList(createOrderRequest.getItems());
        items.forEach(item -> item.setOrder(order));
        order.setItems(items);

        return order;
    }

    @Override
    public Order updateRequestToDomain(Long id, UpdateOrderRequest updateOrderRequest) {
        Order order = new Order(
                id,
                updateOrderRequest.getTitle(),
                updateOrderRequest.getDescription(),
                updateOrderRequest.getOrderStatus(),
                updateOrderRequest.getCpfClient(),
                null,  // Items set below to establish bidirectional relationship
                BigDecimal.ZERO,
                null,
                null,
                null
        );

        // Map items and establish bidirectional relationship
        var items = itemMapper.requestListToDomainList(updateOrderRequest.getItems());
        items.forEach(item -> item.setOrder(order));
        order.setItems(items);

        return order;
    }

    @Override
    public OrderResponse domainToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTitle(),
                order.getDescription(),
                order.getOrderStatus(),
                order.getCpfClient(),
                itemMapper.domainListToResponseList(order.getItems()),
                order.getTotalAmount(),
                order.getReceivedAt(),
                order.getUpdatedAt(),
                order.getRemainingTime()
        );
    }

    @Override
    public OrderEntity domainToEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity(
                order.getId(),
                order.getTitle(),
                order.getDescription(),
                order.getOrderStatus().name(),
                order.getCpfClient(),
                null,  // Items set below to establish bidirectional relationship
                order.getTotalAmount(),
                order.getReceivedAt(),
                order.getRemainingTime(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );

        // Map items and establish bidirectional relationship
        var items = itemMapper.domainListToEntityList(order.getItems());
        items.forEach(item -> item.setOrder(orderEntity));
        orderEntity.setItems(items);

        return orderEntity;
    }

    @Override
    public Order entityToDomain(OrderEntity orderEntity) {
        Order order = new Order(
                orderEntity.getId(),
                orderEntity.getTitle(),
                orderEntity.getDescription(),
                OrderStatus.valueOf(orderEntity.getOrderStatus()),
                orderEntity.getCpfClient(),
                null,  // Items set below to establish bidirectional relationship
                orderEntity.getTotalAmount(),
                orderEntity.getReceivedAt(),
                orderEntity.getCreatedAt(),
                orderEntity.getUpdatedAt()
        );

        // Map items and establish bidirectional relationship
        var items = itemMapper.entityListToDomainList(orderEntity.getItems());
        items.forEach(item -> item.setOrder(order));
        order.setItems(items);

        return order;
    }

    @Override
    public OrderMonitorResponse domainToMonitorResponse(Order order) {
        return new OrderMonitorResponse(
                order.getTitle(),
                order.getDescription(),
                order.getCpfClient(),
                order.getOrderStatus(),
                order.getTotalAmount(),
                order.getReceivedAt(),
                order.getUpdatedAt(),
                order.getRemainingTime()
        );
    }

    @Override
    public Order updateOrderItemsRequestToDomain(Long id, UpdateOrderItemsRequest updateOrderItemsRequest) {
        Order order = new Order(
                id,
                null,
                null,
                null,
                null,
                null,  // Items set below to establish bidirectional relationship
                null,
                null,
                null,
                null
        );

        // Map items and establish bidirectional relationship
        var items = itemMapper.requestListToDomainList(updateOrderItemsRequest.getItems());
        items.forEach(item -> item.setOrder(order));
        order.setItems(items);

        return order;
    }
}
