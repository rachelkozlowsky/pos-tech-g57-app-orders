package br.com.five.seven.food.adapter.in.mappers;

import br.com.five.seven.food.adapter.in.payload.order.CreateOrderRequest;
import br.com.five.seven.food.adapter.in.payload.order.OrderMonitorResponse;
import br.com.five.seven.food.adapter.in.payload.order.OrderResponse;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderItemsRequest;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderRequest;
import br.com.five.seven.food.adapter.out.relational.entity.OrderEntity;
import br.com.five.seven.food.application.domain.Order;

public interface OrderMapper {
    Order createRequestToDomain(CreateOrderRequest createOrderRequest);
    Order updateRequestToDomain(Long id, UpdateOrderRequest updateOrderRequest);
    OrderResponse domainToResponse(Order order);
    OrderEntity domainToEntity(Order order);
    Order entityToDomain(OrderEntity orderEntity);
    OrderMonitorResponse domainToMonitorResponse(Order order);
    Order updateOrderItemsRequestToDomain(Long id, UpdateOrderItemsRequest updateOrderItemsRequest);
}
