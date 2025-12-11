package br.com.five.seven.food.adapter.in.controller;

import br.com.five.seven.food.adapter.in.mappers.OrderMapper;
import br.com.five.seven.food.adapter.in.payload.order.CreateOrderRequest;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderComboRequest;
import br.com.five.seven.food.adapter.in.payload.order.OrderMonitorResponse;
import br.com.five.seven.food.adapter.in.payload.order.OrderResponse;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderRequest;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderStatusRequest;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import br.com.five.seven.food.application.ports.in.OrderServiceIn;
import br.com.five.seven.food.infra.annotations.order.SwaggerCreateOrder;
import br.com.five.seven.food.infra.annotations.order.SwaggerDeleteOrder;
import br.com.five.seven.food.infra.annotations.order.SwaggerGetAllOrders;
import br.com.five.seven.food.infra.annotations.order.SwaggerGetAllOrdersByStatus;
import br.com.five.seven.food.infra.annotations.order.SwaggerGetOrderById;
import br.com.five.seven.food.infra.annotations.order.SwaggerUpdateOrder;
import br.com.five.seven.food.infra.annotations.order.SwaggerUpdateOrderStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Tag(name = "Order", description = "Operations related to order management")
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceIn orderService;

    private final OrderMapper orderMapper;

    @SwaggerGetAllOrders
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderService.findAll(pageable);
        return ResponseEntity.ok(orderPage.map(orderMapper::domainToResponse));
    }

    @SwaggerGetAllOrdersByStatus
    @GetMapping("/status")
    public ResponseEntity<Page<OrderResponse>> getAllOrdersByStatus(@RequestParam List<OrderStatus> status, Pageable pageable) {
        Page<Order> orderPage = orderService.findAllByOrderStatus(status, pageable);
        return ResponseEntity.ok(orderPage.map(orderMapper::domainToResponse));
    }

    @SwaggerGetOrderById
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderMapper.domainToResponse(orderService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/monitor/status")
    public ResponseEntity<Page<OrderMonitorResponse>> getAllOrdersByStatusForMonitor(@RequestParam List<OrderStatus> status, Pageable pageable) {
        Page<Order> orderPage = orderService.findAllByOrderStatus(status, pageable);
        return ResponseEntity.ok(orderPage.map(orderMapper::domainToMonitorResponse));
    }

    @GetMapping("/monitor/{id}")
    public ResponseEntity<OrderMonitorResponse> getOrderByIdForMonitor(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderMapper.domainToMonitorResponse(orderService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @SwaggerCreateOrder
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest order) throws ValidationException {
        var orderCreated = orderService.create(orderMapper.createRequestToDomain(order));
        return ResponseEntity.created(URI.create(orderCreated.getId().toString())).body(orderMapper.domainToResponse(orderCreated));
    }

    @SwaggerUpdateOrder
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @Valid @RequestBody UpdateOrderRequest updateOrderRequest) throws ValidationException {
        Order order = orderService.update(id, orderMapper.updateRequestToDomain(id, updateOrderRequest));
        return ResponseEntity.ok(orderMapper.domainToResponse(order));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/items")
    public ResponseEntity<OrderResponse> updateOrderItems(@PathVariable Long id, @Valid @RequestBody UpdateOrderComboRequest updateOrderComboRequest) throws ValidationException {
        Order order = orderService.updateOrderItems(id, orderMapper.updateOrderItemsRequestToDomain(id, updateOrderComboRequest));
        return ResponseEntity.ok(orderMapper.domainToResponse(order));
    }

    @SwaggerUpdateOrderStatus
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-status")
    public ResponseEntity<OrderStatus> updateOrderStatus(@Valid @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) throws ValidationException {
        Order order = orderService.updateStatusOrder(updateOrderStatusRequest.getId(), updateOrderStatusRequest.getOrderStatus());
        return ResponseEntity.ok(order.getOrderStatus());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/advance-status")
    public ResponseEntity<String> advanceStatus(@PathVariable Long id) {
        try {
            Order updatedOrder = orderService.advanceOrderStatus(id);
            return ResponseEntity.ok("Status do pedido alterado para: " + updatedOrder.getOrderStatus());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @SwaggerDeleteOrder
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


}
