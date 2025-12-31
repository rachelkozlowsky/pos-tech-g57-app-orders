package br.com.five.seven.food.application.service;

import br.com.five.seven.food.adapter.out.api.response.ClientResponse;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Item;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import br.com.five.seven.food.application.ports.in.CategoryServiceIn;
import br.com.five.seven.food.application.ports.out.IClientApiOut;
import br.com.five.seven.food.application.ports.out.IOrderRepositoryOut;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;
import br.com.five.seven.food.infra.exceptions.ClientNotFoundException;
import jakarta.xml.bind.ValidationException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service BDD Tests")
class OrderServiceTest {

    @Mock
    private IOrderRepositoryOut orderRepository;

    @Mock
    private IProductRepositoryOut productRepository;

    @Mock
    private CategoryServiceIn categoryService;

    @Mock
    private IClientApiOut clientApiOut;

    @InjectMocks
    private OrderService orderService;

    // RETRIEVE ORDERS TESTS

    @Test
    @DisplayName("Scenario: Successfully retrieve all orders with pagination")
    void givenMultipleOrders_whenFindingAllOrders_thenPagedOrdersShouldBeReturned() {
        // Given: Multiple orders exist in the system
        Order order1 = createValidOrder(1L, OrderStatus.RECEIVED);
        Order order2 = createValidOrder(2L, OrderStatus.IN_PREPARATION);
        List<Order> orders = Arrays.asList(order1, order2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);

        // When: Finding all orders
        Page<Order> result = orderService.findAll(pageable);

        // Then: Paged orders should be returned
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.getTotalElements(), "Should return correct number of orders");
        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve orders filtered by status")
    void givenOrdersWithSpecificStatus_whenFindingByStatus_thenFilteredOrdersShouldBeReturned() {
        // Given: Orders with specific status exist
        Order order = createValidOrder(1L, OrderStatus.RECEIVED);
        List<OrderStatus> statuses = List.of(OrderStatus.RECEIVED);
        List<Order> orders = List.of(order);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        when(orderRepository.findAllByOrderStatus(anyList(), any(Pageable.class))).thenReturn(orderPage);

        // When: Finding orders by status
        Page<Order> result = orderService.findAllByOrderStatus(statuses, pageable);

        // Then: Filtered orders should be returned
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getTotalElements(), "Should return orders with specified status");
        assertEquals(OrderStatus.RECEIVED, result.getContent().get(0).getOrderStatus());
        verify(orderRepository, times(1)).findAllByOrderStatus(statuses, pageable);
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve order by ID")
    void givenExistingOrderId_whenFindingById_thenOrderShouldBeReturned() {
        // Given: An existing order in the system
        Order order = createValidOrder(1L, OrderStatus.RECEIVED);
        when(orderRepository.findById(1L)).thenReturn(order);

        // When: Finding the order by ID
        Order result = orderService.findById(1L);

        // Then: The order should be returned
        assertNotNull(result, "Retrieved order should not be null");
        assertEquals(1L, result.getId(), "Order ID should match");
        assertEquals(OrderStatus.RECEIVED, result.getOrderStatus(), "Order status should match");
        verify(orderRepository, times(1)).findById(1L);
    }

    // CREATE ORDER TESTS

    @Test
    @DisplayName("Scenario: Successfully create order with valid items")
    void givenValidOrderWithItems_whenCreatingOrder_thenOrderShouldBeCreated() throws ValidationException {
        // Given: A valid order with items
        Order order = createValidOrder(null, OrderStatus.SENT);
        Product product = createValidProduct();
        Category category = createValidCategory();

        when(productRepository.getById(1L)).thenReturn(product);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When: Creating the order
        Order result = orderService.create(order);

        // Then: The order should be created successfully
        assertNotNull(result, "Created order should not be null");
        verify(productRepository, times(1)).getById(1L);
        verify(categoryService, times(1)).getCategoryById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Scenario: Fail to create order without items")
    void givenOrderWithoutItems_whenCreatingOrder_thenValidationExceptionShouldBeThrown() {
        // Given: An order without items
        Order order = new Order();
        order.setItems(new ArrayList<>());

        // When & Then: Creating order should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> orderService.create(order),
            "Should throw ValidationException when order has no items"
        );

        assertEquals("Order must have at least one item.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create order with invalid client CPF")
    void givenOrderWithInvalidClientCpf_whenCreatingOrder_thenClientNotFoundExceptionShouldBeThrown() {
        // Given: An order with invalid client CPF
        Order order = createValidOrder(null, OrderStatus.SENT);
        order.setCpfClient("12345678900");

        when(clientApiOut.getClientByCpf("12345678900")).thenReturn(Optional.empty());

        // When & Then: Creating order should throw ClientNotFoundException
        assertThrows(
            ClientNotFoundException.class,
            () -> orderService.create(order),
            "Should throw ClientNotFoundException for invalid CPF"
        );

        verify(clientApiOut, times(1)).getClientByCpf("12345678900");
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create order with item quantity less than 1")
    void givenOrderWithInvalidItemQuantity_whenCreatingOrder_thenValidationExceptionShouldBeThrown() {
        // Given: An order with item quantity less than 1
        Order order = createValidOrder(null, OrderStatus.SENT);
        order.getItems().get(0).setQuantity(0);

        // When & Then: Creating order should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> orderService.create(order),
            "Should throw ValidationException for invalid quantity"
        );

        assertEquals("Each item must have at least quantity 1.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create order with inactive product")
    void givenOrderWithInactiveProduct_whenCreatingOrder_thenValidationExceptionShouldBeThrown() {
        // Given: An order with an inactive product
        Order order = createValidOrder(null, OrderStatus.SENT);
        Product inactiveProduct = createValidProduct();
        inactiveProduct.setActive(false);

        when(productRepository.getById(1L)).thenReturn(inactiveProduct);

        // When & Then: Creating order should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> orderService.create(order),
            "Should throw ValidationException for inactive product"
        );

        assertEquals("Product 'Hambúrguer' is not available.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create order with product from inactive category")
    void givenOrderWithProductFromInactiveCategory_whenCreatingOrder_thenValidationExceptionShouldBeThrown() {
        // Given: An order with product from inactive category
        Order order = createValidOrder(null, OrderStatus.SENT);
        Product product = createValidProduct();
        Category inactiveCategory = createValidCategory();
        inactiveCategory.setActive(false);

        when(productRepository.getById(1L)).thenReturn(product);
        when(categoryService.getCategoryById(1L)).thenReturn(inactiveCategory);

        // When & Then: Creating order should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> orderService.create(order),
            "Should throw ValidationException for inactive category"
        );

        assertEquals("Category 'Lanches' is not active.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    // UPDATE ORDER TESTS

    @Test
    @DisplayName("Scenario: Successfully update order")
    void givenExistingOrder_whenUpdatingOrder_thenOrderShouldBeUpdated() throws ValidationException {
        // Given: An existing order and update data
        Order existingOrder = createValidOrder(1L, OrderStatus.RECEIVED);
        Order updateData = createValidOrder(null, OrderStatus.IN_PREPARATION);

        when(orderRepository.findById(1L)).thenReturn(existingOrder);
        when(orderRepository.update(any(Order.class))).thenReturn(existingOrder);

        // When: Updating the order
        Order result = orderService.update(1L, updateData);

        // Then: The order should be updated
        assertNotNull(result, "Updated order should not be null");
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).update(any(Order.class));
    }

    @Test
    @DisplayName("Scenario: Successfully update order items")
    void givenExistingOrder_whenUpdatingOrderItems_thenItemsShouldBeUpdated() throws ValidationException {
        // Given: An existing order and new items
        Order existingOrder = createValidOrder(1L, OrderStatus.RECEIVED);
        Order updateData = createValidOrder(null, OrderStatus.RECEIVED);
        Product product = createValidProduct();
        Category category = createValidCategory();

        when(orderRepository.findById(1L)).thenReturn(existingOrder);
        when(productRepository.getById(1L)).thenReturn(product);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(orderRepository.update(any(Order.class))).thenReturn(existingOrder);

        // When: Updating order items
        Order result = orderService.updateOrderItems(1L, updateData);

        // Then: Order items should be updated
        assertNotNull(result, "Updated order should not be null");
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).update(any(Order.class));
    }

    // UPDATE ORDER STATUS TESTS

    @Test
    @DisplayName("Scenario: Successfully update order status to RECEIVED")
    void givenExistingOrder_whenUpdatingStatusToReceived_thenStatusShouldBeUpdatedWithTimestamp() {
        // Given: An existing order
        Order order = createValidOrder(1L, OrderStatus.SENT);
        when(orderRepository.findById(1L)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When: Updating status to RECEIVED
        Order result = orderService.updateStatusOrder(1L, OrderStatus.RECEIVED);

        // Then: Status should be updated with received timestamp
        assertNotNull(result, "Updated order should not be null");
        assertNotNull(result.getReceivedAt(), "Received timestamp should be set");
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Scenario: Successfully advance order status from SENT to RECEIVED")
    void givenOrderWithSentStatus_whenAdvancingStatus_thenStatusShouldBecomeReceived() {
        // Given: An order with SENT status
        Order order = createValidOrder(1L, OrderStatus.SENT);
        when(orderRepository.findById(1L)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When: Advancing the order status
        Order result = orderService.advanceOrderStatus(1L);

        // Then: Status should advance to RECEIVED
        assertEquals(OrderStatus.RECEIVED, result.getOrderStatus(), "Status should be RECEIVED");
        assertNotNull(result.getReceivedAt(), "Received timestamp should be set");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Scenario: Successfully advance order status from RECEIVED to IN_PREPARATION")
    void givenOrderWithReceivedStatus_whenAdvancingStatus_thenStatusShouldBecomeInPreparation() {
        // Given: An order with RECEIVED status
        Order order = createValidOrder(1L, OrderStatus.RECEIVED);
        when(orderRepository.findById(1L)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When: Advancing the order status
        Order result = orderService.advanceOrderStatus(1L);

        // Then: Status should advance to IN_PREPARATION
        assertEquals(OrderStatus.IN_PREPARATION, result.getOrderStatus(), "Status should be IN_PREPARATION");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Scenario: Successfully advance order status from IN_PREPARATION to READY")
    void givenOrderWithInPreparationStatus_whenAdvancingStatus_thenStatusShouldBecomeReady() {
        // Given: An order with IN_PREPARATION status
        Order order = createValidOrder(1L, OrderStatus.IN_PREPARATION);
        when(orderRepository.findById(1L)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When: Advancing the order status
        Order result = orderService.advanceOrderStatus(1L);

        // Then: Status should advance to READY
        assertEquals(OrderStatus.READY, result.getOrderStatus(), "Status should be READY");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Scenario: Successfully advance order status from READY to FINISHED")
    void givenOrderWithReadyStatus_whenAdvancingStatus_thenStatusShouldBecomeFinished() {
        // Given: An order with READY status
        Order order = createValidOrder(1L, OrderStatus.READY);
        when(orderRepository.findById(1L)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When: Advancing the order status
        Order result = orderService.advanceOrderStatus(1L);

        // Then: Status should advance to FINISHED
        assertEquals(OrderStatus.FINISHED, result.getOrderStatus(), "Status should be FINISHED");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Scenario: Fail to advance order status from FINISHED")
    void givenOrderWithFinishedStatus_whenAdvancingStatus_thenIllegalStateExceptionShouldBeThrown() {
        // Given: An order with FINISHED status
        Order order = createValidOrder(1L, OrderStatus.FINISHED);
        when(orderRepository.findById(1L)).thenReturn(order);

        // When & Then: Advancing status should throw IllegalStateException
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> orderService.advanceOrderStatus(1L),
            "Should throw IllegalStateException for finished order"
        );

        assertEquals("Não é possível avançar o status deste pedido.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    // DELETE ORDER TESTS

    @Test
    @DisplayName("Scenario: Successfully delete order by ID")
    void givenExistingOrderId_whenDeletingOrder_thenOrderShouldBeDeleted() {
        // Given: An existing order
        doNothing().when(orderRepository).delete(1L);

        // When: Deleting the order
        orderService.deleteById(1L);

        // Then: The order should be deleted
        verify(orderRepository, times(1)).delete(1L);
    }

    // CALCULATE ORDER TIME TESTS

    @Test
    @DisplayName("Scenario: Calculate remaining time for order in preparation")
    void givenOrderInPreparation_whenCalculatingTime_thenRemainingTimeShouldBeReturned() {
        // Given: An order received 10 minutes ago
        LocalDateTime receivedAt = LocalDateTime.now().minusMinutes(10);

        // When: Calculating remaining time
        String result = OrderService.calculateTime(receivedAt, OrderStatus.IN_PREPARATION);

        // Then: Remaining time should be approximately 20 minutes
        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains("Tempo restante:"), "Should contain remaining time message");
    }

    @Test
    @DisplayName("Scenario: Display ready message for READY order")
    void givenOrderWithReadyStatus_whenCalculatingTime_thenReadyMessageShouldBeReturned() {
        // Given: An order with READY status
        LocalDateTime receivedAt = LocalDateTime.now().minusMinutes(25);

        // When: Calculating time
        String result = OrderService.calculateTime(receivedAt, OrderStatus.READY);

        // Then: Ready message should be returned
        assertEquals("Pedindo pronto para retirada", result, "Should return ready message");
    }

    @Test
    @DisplayName("Scenario: Display finished message for FINISHED order")
    void givenOrderWithFinishedStatus_whenCalculatingTime_thenFinishedMessageShouldBeReturned() {
        // Given: An order with FINISHED status
        LocalDateTime receivedAt = LocalDateTime.now().minusMinutes(30);

        // When: Calculating time
        String result = OrderService.calculateTime(receivedAt, OrderStatus.FINISHED);

        // Then: Finished message should be returned
        assertEquals("Pedido entregue ao cliente", result, "Should return finished message");
    }

    @Test
    @DisplayName("Scenario: Display expired message when preparation time exceeded")
    void givenOrderWithExpiredTime_whenCalculatingTime_thenExpiredMessageShouldBeReturned() {
        // Given: An order received more than 30 minutes ago
        LocalDateTime receivedAt = LocalDateTime.now().minusMinutes(35);

        // When: Calculating time
        String result = OrderService.calculateTime(receivedAt, OrderStatus.IN_PREPARATION);

        // Then: Expired message should be returned
        assertEquals("O prazo de preparacao do pedido expirou", result, "Should return expired message");
    }

    @Test
    @DisplayName("Scenario: Return null when initial time is null")
    void givenNullInitialTime_whenCalculatingTime_thenNullShouldBeReturned() {
        // Given: Null initial time
        LocalDateTime receivedAt = null;

        // When: Calculating time
        String result = OrderService.calculateTime(receivedAt, OrderStatus.SENT);

        // Then: Null should be returned
        assertNull(result, "Should return null when initial time is null");
    }

    @Test
    @DisplayName("Scenario: Fail to advance order status when status is null")
    void givenOrderWithNullStatus_whenAdvancingStatus_thenIllegalStateExceptionShouldBeThrown() {
        // Given: An order with null status
        Order order = createValidOrder(1L, OrderStatus.SENT);
        order.setOrderStatus(null);
        when(orderRepository.findById(1L)).thenReturn(order);

        // When & Then: Advancing status should throw IllegalStateException
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> orderService.advanceOrderStatus(1L),
            "Should throw IllegalStateException for null status"
        );

        assertEquals("A ordem não possui status.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Successfully create order with valid client CPF")
    void givenOrderWithValidClientCpf_whenCreatingOrder_thenOrderShouldBeCreated() throws ValidationException {
        // Given: An order with valid client CPF
        Order order = createValidOrder(null, OrderStatus.SENT);
        order.setCpfClient("12345678900");
        Product product = createValidProduct();
        Category category = createValidCategory();

        ClientResponse clientResponse = new ClientResponse();
        clientResponse.setCpf("12345678900");
        when(clientApiOut.getClientByCpf("12345678900")).thenReturn(Optional.of(clientResponse));
        when(productRepository.getById(1L)).thenReturn(product);
        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When: Creating the order
        Order result = orderService.create(order);

        // Then: The order should be created successfully
        assertNotNull(result, "Created order should not be null");
        verify(clientApiOut, times(1)).getClientByCpf("12345678900");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Scenario: Fail to create order when product is not found")
    void givenOrderWithNonExistentProduct_whenCreatingOrder_thenValidationExceptionShouldBeThrown() {
        // Given: An order with non-existent product
        Order order = createValidOrder(null, OrderStatus.SENT);

        when(productRepository.getById(1L)).thenReturn(null);

        // When & Then: Creating order should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> orderService.create(order),
            "Should throw ValidationException for non-existent product"
        );

        assertEquals("Product with ID 1 not found.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create order when product has no category")
    void givenOrderWithProductWithoutCategory_whenCreatingOrder_thenValidationExceptionShouldBeThrown() {
        // Given: An order with product without category
        Order order = createValidOrder(null, OrderStatus.SENT);
        Product productWithoutCategory = createValidProduct();
        productWithoutCategory.setCategory(null);

        when(productRepository.getById(1L)).thenReturn(productWithoutCategory);

        // When & Then: Creating order should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> orderService.create(order),
            "Should throw ValidationException for product without category"
        );

        assertEquals("Product 'Hambúrguer' does not have a category assigned.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create order when category not found")
    void givenOrderWithProductFromNonExistentCategory_whenCreatingOrder_thenValidationExceptionShouldBeThrown() {
        // Given: An order with product from non-existent category
        Order order = createValidOrder(null, OrderStatus.SENT);
        Product product = createValidProduct();

        when(productRepository.getById(1L)).thenReturn(product);
        when(categoryService.getCategoryById(1L)).thenReturn(null);

        // When & Then: Creating order should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> orderService.create(order),
            "Should throw ValidationException when category not found"
        );

        assertEquals("Category for product 'Hambúrguer' not found.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Successfully update order status to other status")
    void givenExistingOrder_whenUpdatingStatusToInPreparation_thenStatusShouldBeUpdated() {
        // Given: An existing order
        Order order = createValidOrder(1L, OrderStatus.RECEIVED);
        when(orderRepository.findById(1L)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When: Updating status to IN_PREPARATION
        Order result = orderService.updateStatusOrder(1L, OrderStatus.IN_PREPARATION);

        // Then: Status should be updated
        assertNotNull(result, "Updated order should not be null");
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // Helper methods
    private Order createValidOrder(Long id, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setOrderStatus(status);
        order.setItems(new ArrayList<>());

        Product product = createValidProduct();
        Item item = new Item();
        item.setProduct(product);
        item.setQuantity(2);
        order.getItems().add(item);

        if (status == OrderStatus.RECEIVED || status == OrderStatus.IN_PREPARATION ||
            status == OrderStatus.READY || status == OrderStatus.FINISHED) {
            order.setReceivedAt(LocalDateTime.now().minusMinutes(10));
        }

        return order;
    }

    private Product createValidProduct() {
        Category category = createValidCategory();

        Product product = new Product();
        product.setId(1L);
        product.setName("Hambúrguer");
        product.setDescription("Delicioso hambúrguer");
        product.setPrice(BigDecimal.valueOf(25.90));
        product.setCategory(category);
        product.setActive(true);

        return product;
    }

    private Category createValidCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");
        category.setActive(true);

        return category;
    }
}

