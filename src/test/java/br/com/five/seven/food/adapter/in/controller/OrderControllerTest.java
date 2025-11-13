package br.com.five.seven.food.adapter.in.controller;

import br.com.five.seven.food.adapter.in.mappers.OrderMapper;
import br.com.five.seven.food.adapter.in.payload.combo.ComboRequest;
import br.com.five.seven.food.adapter.in.payload.order.CreateOrderRequest;
import br.com.five.seven.food.adapter.in.payload.order.OrderResponse;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderRequest;
import br.com.five.seven.food.application.domain.Combo;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import br.com.five.seven.food.application.ports.in.OrderServiceIn;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderServiceIn orderService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderController orderController;

    @Test
    void shouldCreateOrderSuccessfully() throws ValidationException {
        Order order = createTestOrder();
        CreateOrderRequest createOrderRequest = createTestCreateOrderRequest();

        when(orderService.create(order)).thenReturn(order);
        when(orderMapper.createRequestToDomain(createOrderRequest)).thenReturn(order);

        ResponseEntity<?> response = orderController.createOrder(createOrderRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(orderService, times(1)).create(order);
    }

    @Test
    void shouldFindAllOrdersSuccessfully() {
        Order order = createTestOrder();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

        when(orderService.findAll(pageable)).thenReturn(orderPage);
        when(orderMapper.domainToResponse(order)).thenReturn(mock(OrderResponse.class));

        ResponseEntity<Page<OrderResponse>> response = orderController.getAllOrders(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService, times(1)).findAll(pageable);
    }

    @Test
    void shouldFindOrderByIdSuccessfully() throws ValidationException {
        Long id = 1L;
        Order order = createTestOrder();
        OrderResponse orderResponse = mock(OrderResponse.class);

        when(orderService.findById(id)).thenReturn(order);
        when(orderMapper.domainToResponse(order)).thenReturn(orderResponse);

        ResponseEntity<OrderResponse> response = orderController.getOrderById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService, times(1)).findById(id);
    }

    @Test
    void shouldUpdateOrderSuccessfully() throws ValidationException {
        Long id = 1L;
        Order order = createTestOrder();
        UpdateOrderRequest updateOrderRequest = mock(UpdateOrderRequest.class);
        OrderResponse orderResponse = mock(OrderResponse.class);

        when(orderService.update(id, order)).thenReturn(order);
        when(orderMapper.updateRequestToDomain(id, updateOrderRequest)).thenReturn(order);
        when(orderMapper.domainToResponse(order)).thenReturn(orderResponse);

        ResponseEntity<OrderResponse> response = orderController.updateOrder(id, updateOrderRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService, times(1)).update(id, order);
    }

    @Test
    void shouldDeleteOrderSuccessfully() {
        Long id = 1L;
        ResponseEntity<Void> response = orderController.deleteOrder(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteById(id);
    }

    private CreateOrderRequest createTestCreateOrderRequest() {
        return new CreateOrderRequest(
                "cpf",
                "title",
                "description",
                mock(ComboRequest.class)
        );
    }

    private Order createTestOrder() {
        return new Order(
                1L,
                "title",
                "description",
                OrderStatus.IN_PREPARATION,
                "56040817011s",
                mock(Combo.class),
                BigDecimal.ONE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
