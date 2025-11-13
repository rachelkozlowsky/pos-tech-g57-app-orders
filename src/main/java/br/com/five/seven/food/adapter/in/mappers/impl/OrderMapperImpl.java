package br.com.five.seven.food.adapter.in.mappers.impl;

import br.com.five.seven.food.adapter.in.mappers.ComboMapper;
import br.com.five.seven.food.adapter.in.mappers.OrderMapper;
import br.com.five.seven.food.adapter.in.payload.combo.ComboRequest;
import br.com.five.seven.food.adapter.in.payload.order.CreateOrderRequest;
import br.com.five.seven.food.adapter.in.payload.order.OrderMonitorResponse;
import br.com.five.seven.food.adapter.in.payload.order.OrderResponse;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderRequest;
import br.com.five.seven.food.adapter.out.relational.entity.OrderEntity;
import br.com.five.seven.food.application.domain.Combo;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderMapperImpl implements OrderMapper {

    private final ComboMapper comboMapper;

    @Override
    public Order createRequestToDomain(CreateOrderRequest createOrderRequest) {
        Combo combo = comboMapper.requestToDomain(createOrderRequest.getCombo());

        return new Order(
                null,
                createOrderRequest.getTitle(),
                createOrderRequest.getDescription(),
                OrderStatus.CREATED,
                createOrderRequest.getCpfClient(),
                combo,
                BigDecimal.ZERO,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Override
    public Order updateRequestToDomain(Long id, UpdateOrderRequest updateOrderRequest) {
        Combo combo = comboMapper.requestToDomain(updateOrderRequest.getCombo());

        return new Order(
                id,
                updateOrderRequest.getTitle(),
                updateOrderRequest.getDescription(),
                updateOrderRequest.getOrderStatus(),
                updateOrderRequest.getCpfClient(),
                combo,
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
                comboMapper.domainToResponse(order.getCombo()),
                order.getTotalAmount(),
                order.getUpdatedAt(),
                order.getUpdatedAt(),
                order.getRemainingTime()
        );
    }

    @Override
    public OrderEntity domainToEntity(Order order) {
        return new OrderEntity(
                order.getId(),
                order.getTitle(),
                order.getDescription(),
                order.getOrderStatus().name(),
                order.getCpfClient(),
                comboMapper.domainToEntity(order.getCombo()),
                order.getTotalAmount(),
                order.getReceivedAt(),
                order.getRemainingTime(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    @Override
    public Order entityToDomain(OrderEntity orderEntity) {
        Combo combo = comboMapper.entityToDomain(orderEntity.getCombo());

        return new Order(
                orderEntity.getId(),
                orderEntity.getTitle(),
                orderEntity.getDescription(),
                OrderStatus.valueOf(orderEntity.getOrderStatus()),
                orderEntity.getCpfClient(),
                combo,
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
    public Order updateOrderComboRequestToDomain(Long id, ComboRequest comboRequest) {
        Combo combo = comboMapper.requestToDomain(comboRequest);

        return new Order(
                id,
                null,
                null,
                null,
                null,
                combo,
                null,
                null,
                null,
                null
        );
    }
}
