package br.com.five.seven.food.adapter.in.mappers.impl;

import br.com.five.seven.food.adapter.in.mappers.ItemMapper;
import br.com.five.seven.food.adapter.in.mappers.OrderMapper;
import br.com.five.seven.food.adapter.in.payload.order.CreateOrderRequest;
import br.com.five.seven.food.adapter.in.payload.order.OrderMonitorResponse;
import br.com.five.seven.food.adapter.in.payload.order.OrderResponse;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderComboRequest;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderRequest;
import br.com.five.seven.food.adapter.out.relational.entity.ItemEntity;
import br.com.five.seven.food.adapter.out.relational.entity.OrderEntity;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapperImpl implements OrderMapper {

    private final ItemMapper itemMapper;

    @Override
    public Order createRequestToDomain(CreateOrderRequest createOrderRequest) {
        return new Order(
                null,
                createOrderRequest.getTitle(),
                createOrderRequest.getDescription(),
                OrderStatus.CREATED,
                createOrderRequest.getCpfClient(),
                itemMapper.requestListToDomainList(createOrderRequest.getItems()),
                BigDecimal.ZERO,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Override
    public Order updateRequestToDomain(Long id, UpdateOrderRequest updateOrderRequest) {
        return new Order(
                id,
                updateOrderRequest.getTitle(),
                updateOrderRequest.getDescription(),
                updateOrderRequest.getOrderStatus(),
                updateOrderRequest.getCpfClient(),
                itemMapper.requestListToDomainList(updateOrderRequest.getItems()),
                BigDecimal.ZERO,
                null,
                null,
                null
        );
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
                null,
                order.getTotalAmount(),
                order.getReceivedAt(),
                order.getRemainingTime(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );

        // Map items and establish bidirectional relationship
        var items = order.getItems().stream()
                .map(item -> {
                    ItemEntity itemEntity = itemMapper.domainToEntity(item);
                    itemEntity.setOrder(orderEntity);
                    return itemEntity;
                })
                .collect(Collectors.toList());

        orderEntity.setItems(items);
        return orderEntity;
    }

    @Override
    public Order entityToDomain(OrderEntity orderEntity) {
        return new Order(
                orderEntity.getId(),
                orderEntity.getTitle(),
                orderEntity.getDescription(),
                OrderStatus.valueOf(orderEntity.getOrderStatus()),
                orderEntity.getCpfClient(),
                itemMapper.entityListToDomainList(orderEntity.getItems()),
                orderEntity.getTotalAmount(),
                orderEntity.getReceivedAt(),
                orderEntity.getCreatedAt(),
                orderEntity.getUpdatedAt()
        );
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
    public Order updateOrderItemsRequestToDomain(Long id, UpdateOrderComboRequest updateOrderComboRequest) {
        return new Order(
                id,
                null,
                null,
                null,
                null,
                itemMapper.requestListToDomainList(updateOrderComboRequest.getItems()),
                null,
                null,
                null,
                null
        );
    }
}
