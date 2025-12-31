package br.com.five.seven.food.adapter.in.controller;

import br.com.five.seven.food.adapter.in.mappers.OrderMapper;
import br.com.five.seven.food.adapter.in.payload.item.ItemRequest;
import br.com.five.seven.food.adapter.in.payload.order.CreateOrderRequest;
import br.com.five.seven.food.adapter.in.payload.order.OrderMonitorResponse;
import br.com.five.seven.food.adapter.in.payload.order.OrderResponse;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderRequest;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderItemsRequest;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderStatusRequest;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Item;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import br.com.five.seven.food.application.ports.in.OrderServiceIn;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Controller BDD Tests")
class OrderControllerTest {

    @Mock
    private OrderServiceIn orderService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderController orderController;


    // RETRIEVE ORDERS TESTS

    @Test
    @DisplayName("Scenario: Successfully retrieve all orders with pagination")
    void givenMultipleOrders_whenGettingAllOrders_thenPagedOrdersShouldBeReturned() {
        // Given: Multiple orders exist
        Order order1 = createOrder(1L, OrderStatus.RECEIVED);
        Order order2 = createOrder(2L, OrderStatus.IN_PREPARATION);
        List<Order> orders = Arrays.asList(order1, order2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        OrderResponse response1 = createOrderResponse(1L, OrderStatus.RECEIVED);
        OrderResponse response2 = createOrderResponse(2L, OrderStatus.IN_PREPARATION);

        when(orderService.findAll(any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.domainToResponse(order1)).thenReturn(response1);
        when(orderMapper.domainToResponse(order2)).thenReturn(response2);

        // When: Getting all orders
        ResponseEntity<Page<OrderResponse>> response = orderController.getAllOrders(pageable);

        // Then: Paged orders should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        verify(orderService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve orders filtered by status")
    void givenOrdersWithSpecificStatus_whenGettingOrdersByStatus_thenFilteredOrdersShouldBeReturned() {
        // Given: Orders with specific status
        Order order = createOrder(1L, OrderStatus.RECEIVED);
        List<Order> orders = List.of(order);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        OrderResponse orderResponse = createOrderResponse(1L, OrderStatus.RECEIVED);

        when(orderService.findAllByOrderStatus(anyList(), any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.domainToResponse(order)).thenReturn(orderResponse);

        // When: Getting orders by status
        ResponseEntity<Page<OrderResponse>> response = orderController.getAllOrdersByStatus(
                List.of(OrderStatus.RECEIVED), pageable);

        // Then: Filtered orders should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(OrderStatus.RECEIVED, response.getBody().getContent().get(0).getOrderStatus());
        verify(orderService, times(1)).findAllByOrderStatus(anyList(), any(Pageable.class));
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve order by ID")
    void givenExistingOrderId_whenGettingOrderById_thenOrderShouldBeReturned() throws ValidationException {
        // Given: An existing order
        Order order = createOrder(1L, OrderStatus.RECEIVED);
        OrderResponse orderResponse = createOrderResponse(1L, OrderStatus.RECEIVED);

        when(orderService.findById(1L)).thenReturn(order);
        when(orderMapper.domainToResponse(order)).thenReturn(orderResponse);

        // When: Getting order by ID
        ResponseEntity<OrderResponse> response = orderController.getOrderById(1L);

        // Then: Order should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(OrderStatus.RECEIVED, response.getBody().getOrderStatus());
        verify(orderService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Scenario: Return 404 when order not found by ID")
    void givenNonExistingOrderId_whenGettingOrderById_thenNotFoundShouldBeReturned() throws ValidationException {
        // Given: A non-existing order ID
        when(orderService.findById(999L)).thenThrow(new RuntimeException("Order not found"));

        // When: Getting order by ID
        ResponseEntity<OrderResponse> response = orderController.getOrderById(999L);

        // Then: 404 should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Scenario: Return empty list when no orders exist")
    void givenNoOrders_whenGettingAllOrders_thenEmptyListShouldBeReturned() {
        // Given: No orders exist
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(orderService.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When: Getting all orders
        ResponseEntity<Page<OrderResponse>> response = orderController.getAllOrders(pageable);

        // Then: Empty list should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getContent().size());
    }

    @Test
    @DisplayName("Scenario: Return empty list when filtering by non-matching status")
    void givenNoMatchingOrders_whenFilteringByStatus_thenEmptyListShouldBeReturned() {
        // Given: No orders with the specified status
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(orderService.findAllByOrderStatus(anyList(), any(Pageable.class))).thenReturn(emptyPage);

        // When: Filtering by status
        ResponseEntity<Page<OrderResponse>> response = orderController.getAllOrdersByStatus(
                List.of(OrderStatus.FINISHED), pageable);

        // Then: Empty list should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getContent().size());
    }


    // CREATE ORDER TESTS

    @Test
    @DisplayName("Scenario: Successfully create order with valid items")
    void givenValidOrderRequest_whenCreatingOrder_thenOrderShouldBeCreatedAndReturned() throws ValidationException {
        // Given: A valid order request
        CreateOrderRequest request = new CreateOrderRequest();
        request.setTitle("Pedido 1");
        request.setDescription("Pedido de teste");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        request.setItems(List.of(itemRequest));

        Order createdOrder = createOrder(1L, OrderStatus.SENT);
        OrderResponse orderResponse = createOrderResponse(1L, OrderStatus.SENT);

        when(orderMapper.createRequestToDomain(any(CreateOrderRequest.class))).thenReturn(createdOrder);
        when(orderService.create(any(Order.class))).thenReturn(createdOrder);
        when(orderMapper.domainToResponse(createdOrder)).thenReturn(orderResponse);

        // When: Creating the order
        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        // Then: Order should be created and returned
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(OrderStatus.SENT, response.getBody().getOrderStatus());
        verify(orderService, times(1)).create(any(Order.class));
    }

    @Test
    @DisplayName("Scenario: Successfully create order with multiple items")
    void givenOrderWithMultipleItems_whenCreatingOrder_thenOrderShouldBeCreated() throws ValidationException {
        // Given: An order with multiple items
        ItemRequest item1 = new ItemRequest();
        item1.setProductId(1L);
        item1.setQuantity(2);

        ItemRequest item2 = new ItemRequest();
        item2.setProductId(2L);
        item2.setQuantity(3);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setTitle("Pedido múltiplo");
        request.setItems(Arrays.asList(item1, item2));

        Order createdOrder = createOrder(1L, OrderStatus.SENT);
        OrderResponse orderResponse = createOrderResponse(1L, OrderStatus.SENT);

        when(orderMapper.createRequestToDomain(any(CreateOrderRequest.class))).thenReturn(createdOrder);
        when(orderService.create(any(Order.class))).thenReturn(createdOrder);
        when(orderMapper.domainToResponse(createdOrder)).thenReturn(orderResponse);

        // When: Creating the order
        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        // Then: Order should be created
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("Scenario: Successfully create order with client CPF")
    void givenOrderWithClientCpf_whenCreatingOrder_thenOrderShouldBeCreated() throws ValidationException {
        // Given: An order with client CPF
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setTitle("Pedido identificado");
        request.setCpfClient("12345678900");
        request.setItems(List.of(itemRequest));

        Order createdOrder = createOrder(1L, OrderStatus.SENT);
        createdOrder.setCpfClient("12345678900");
        OrderResponse orderResponse = createOrderResponse(1L, OrderStatus.SENT);
        orderResponse.setClient("12345678900");

        when(orderMapper.createRequestToDomain(any(CreateOrderRequest.class))).thenReturn(createdOrder);
        when(orderService.create(any(Order.class))).thenReturn(createdOrder);
        when(orderMapper.domainToResponse(createdOrder)).thenReturn(orderResponse);

        // When: Creating the order
        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        // Then: Order should be created with CPF
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("12345678900", response.getBody().getClient());
    }


    // UPDATE ORDER STATUS TESTS

    @Test
    @DisplayName("Scenario: Successfully update order status")
    void givenValidStatusUpdate_whenUpdatingOrderStatus_thenStatusShouldBeUpdated() throws ValidationException {
        // Given: A valid status update request
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setId(1L);
        request.setOrderStatus(OrderStatus.IN_PREPARATION);

        Order updatedOrder = createOrder(1L, OrderStatus.IN_PREPARATION);
        when(orderService.updateStatusOrder(1L, OrderStatus.IN_PREPARATION)).thenReturn(updatedOrder);

        // When: Updating order status
        ResponseEntity<OrderStatus> response = orderController.updateOrderStatus(request);

        // Then: Status should be updated
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.IN_PREPARATION, response.getBody());
        verify(orderService, times(1)).updateStatusOrder(1L, OrderStatus.IN_PREPARATION);
    }

    @Test
    @DisplayName("Scenario: Successfully advance order status")
    void givenExistingOrder_whenAdvancingStatus_thenStatusShouldBeAdvanced() throws ValidationException {
        // Given: An existing order
        Order updatedOrder = createOrder(1L, OrderStatus.RECEIVED);
        when(orderService.advanceOrderStatus(1L)).thenReturn(updatedOrder);

        // When: Advancing status
        ResponseEntity<String> response = orderController.advanceStatus(1L);

        // Then: Status should be advanced
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("RECEIVED"));
        verify(orderService, times(1)).advanceOrderStatus(1L);
    }

    @Test
    @DisplayName("Scenario: Fail to advance status of finished order")
    void givenFinishedOrder_whenAdvancingStatus_thenBadRequestShouldBeReturned() throws ValidationException {
        // Given: A finished order
        when(orderService.advanceOrderStatus(1L))
                .thenThrow(new IllegalStateException("Não é possível avançar o status deste pedido."));

        // When: Attempting to advance status
        ResponseEntity<String> response = orderController.advanceStatus(1L);

        // Then: Bad request should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Não é possível avançar"));
        verify(orderService, times(1)).advanceOrderStatus(1L);
    }


    // DELETE ORDER TESTS

    @Test
    @DisplayName("Scenario: Successfully delete order")
    void givenExistingOrder_whenDeletingOrder_thenOrderShouldBeDeleted() {
        // Given: An existing order
        doNothing().when(orderService).deleteById(1L);

        // When: Deleting the order
        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        // Then: Order should be deleted
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Scenario: Return 404 when deleting non-existing order")
    void givenNonExistingOrder_whenDeletingOrder_thenNotFoundShouldBeReturned() {
        // Given: A non-existing order
        doThrow(new RuntimeException("Order not found")).when(orderService).deleteById(999L);

        // When: Attempting to delete
        ResponseEntity<Void> response = orderController.deleteOrder(999L);

        // Then: 404 should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService, times(1)).deleteById(999L);
    }

    // UPDATE ORDER TESTS

    @Test
    @DisplayName("Scenario: Successfully update order")
    void givenExistingOrder_whenUpdatingOrder_thenOrderShouldBeUpdated() throws ValidationException {
        // Given: An existing order and update data
        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");

        Order updatedOrder = createOrder(1L, OrderStatus.RECEIVED);
        OrderResponse orderResponse = createOrderResponse(1L, OrderStatus.RECEIVED);

        when(orderMapper.updateRequestToDomain(anyLong(), any(UpdateOrderRequest.class))).thenReturn(updatedOrder);
        when(orderService.update(anyLong(), any(Order.class))).thenReturn(updatedOrder);
        when(orderMapper.domainToResponse(updatedOrder)).thenReturn(orderResponse);

        // When: Updating the order
        ResponseEntity<OrderResponse> response = orderController.updateOrder(1L, request);

        // Then: Order should be updated
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(orderService, times(1)).update(anyLong(), any(Order.class));
    }

    @Test
    @DisplayName("Scenario: Successfully update order items")
    void givenExistingOrderAndNewItems_whenUpdatingOrderItems_thenItemsShouldBeUpdated() throws ValidationException {
        // Given: An existing order and new items
        UpdateOrderItemsRequest request = new UpdateOrderItemsRequest();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setProductId(2L);
        itemRequest.setQuantity(5);
        request.setItems(List.of(itemRequest));

        Order updatedOrder = createOrder(1L, OrderStatus.RECEIVED);
        OrderResponse orderResponse = createOrderResponse(1L, OrderStatus.RECEIVED);

        when(orderMapper.updateOrderItemsRequestToDomain(anyLong(), any(UpdateOrderItemsRequest.class)))
                .thenReturn(updatedOrder);
        when(orderService.updateOrderItems(anyLong(), any(Order.class))).thenReturn(updatedOrder);
        when(orderMapper.domainToResponse(updatedOrder)).thenReturn(orderResponse);

        // When: Updating order items
        ResponseEntity<OrderResponse> response = orderController.updateOrderItems(1L, request);

        // Then: Items should be updated
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(orderService, times(1)).updateOrderItems(anyLong(), any(Order.class));
    }

    // MONITOR TESTS

    @Test
    @DisplayName("Scenario: Successfully retrieve orders for monitor by status")
    void givenOrdersWithStatus_whenGettingOrdersForMonitor_thenMonitorResponseShouldBeReturned() {
        // Given: Orders with specific status
        Order order = createOrder(1L, OrderStatus.RECEIVED);
        List<Order> orders = List.of(order);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        OrderMonitorResponse monitorResponse = createOrderMonitorResponse(1L, OrderStatus.RECEIVED);

        when(orderService.findAllByOrderStatus(anyList(), any(Pageable.class))).thenReturn(orderPage);
        when(orderMapper.domainToMonitorResponse(order)).thenReturn(monitorResponse);

        // When: Getting orders for monitor
        ResponseEntity<Page<OrderMonitorResponse>> response = orderController.getAllOrdersByStatusForMonitor(
                List.of(OrderStatus.RECEIVED), pageable);

        // Then: Monitor response should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        verify(orderService, times(1)).findAllByOrderStatus(anyList(), any(Pageable.class));
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve order by ID for monitor")
    void givenExistingOrderId_whenGettingOrderByIdForMonitor_thenMonitorResponseShouldBeReturned() throws ValidationException {
        // Given: An existing order
        Order order = createOrder(1L, OrderStatus.RECEIVED);
        OrderMonitorResponse monitorResponse = createOrderMonitorResponse(1L, OrderStatus.RECEIVED);

        when(orderService.findById(1L)).thenReturn(order);
        when(orderMapper.domainToMonitorResponse(order)).thenReturn(monitorResponse);

        // When: Getting order by ID for monitor
        ResponseEntity<OrderMonitorResponse> response = orderController.getOrderByIdForMonitor(1L);

        // Then: Monitor response should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderStatus.RECEIVED, response.getBody().getOrderStatus());
        verify(orderService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Scenario: Return 404 when order not found by ID for monitor")
    void givenNonExistingOrderId_whenGettingOrderByIdForMonitor_thenNotFoundShouldBeReturned() throws ValidationException {
        // Given: A non-existing order ID
        when(orderService.findById(999L)).thenThrow(new RuntimeException("Order not found"));

        // When: Getting order by ID for monitor
        ResponseEntity<OrderMonitorResponse> response = orderController.getOrderByIdForMonitor(999L);

        // Then: 404 should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(orderService, times(1)).findById(999L);
    }



    // Helper methods
    private Order createOrder(Long id, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setOrderStatus(status);
        order.setItems(new ArrayList<>());

        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");

        Product product = new Product();
        product.setId(1L);
        product.setName("Hambúrguer");
        product.setPrice(BigDecimal.valueOf(25.90));
        product.setCategory(category);

        Item item = new Item();
        item.setProduct(product);
        item.setQuantity(2);
        order.getItems().add(item);

        return order;
    }

    private OrderResponse createOrderResponse(Long id, OrderStatus status) {
        OrderResponse response = new OrderResponse();
        response.setId(id);
        response.setOrderStatus(status);
        return response;
    }

    private OrderMonitorResponse createOrderMonitorResponse(Long id, OrderStatus status) {
        OrderMonitorResponse response = new OrderMonitorResponse();
        response.setTitle("Pedido Monitor");
        response.setDescription("Descrição");
        response.setClientCpf("12345678900");
        response.setOrderStatus(status);
        response.setTotalAmount(BigDecimal.valueOf(50.00));
        return response;
    }
}

