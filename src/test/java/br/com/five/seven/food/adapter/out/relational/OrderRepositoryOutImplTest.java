package br.com.five.seven.food.adapter.out.relational;

import br.com.five.seven.food.adapter.in.mappers.OrderMapper;
import br.com.five.seven.food.adapter.out.relational.entity.OrderEntity;
import br.com.five.seven.food.adapter.out.relational.repository.OrderRepository;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Repository Out Tests")
class OrderRepositoryOutImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderRepositoryOutImpl orderRepositoryOut;

    @Test
    @DisplayName("Should find all orders with pagination")
    void givenPageable_whenFindingAll_thenPagedOrdersShouldBeReturned() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        OrderEntity entity1 = createOrderEntity(1L, OrderStatus.RECEIVED);
        OrderEntity entity2 = createOrderEntity(2L, OrderStatus.IN_PREPARATION);
        Page<OrderEntity> entityPage = new PageImpl<>(Arrays.asList(entity1, entity2), pageable, 2);

        Order order1 = createOrder(1L, OrderStatus.RECEIVED);
        Order order2 = createOrder(2L, OrderStatus.IN_PREPARATION);

        when(orderRepository.findAll(pageable)).thenReturn(entityPage);
        when(orderMapper.entityToDomain(entity1)).thenReturn(order1);
        when(orderMapper.entityToDomain(entity2)).thenReturn(order2);

        // When
        Page<Order> result = orderRepositoryOut.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should find all orders by status")
    void givenOrderStatus_whenFindingByStatus_thenFilteredOrdersShouldBeReturned() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<OrderStatus> statuses = List.of(OrderStatus.RECEIVED);
        OrderEntity entity1 = createOrderEntity(1L, OrderStatus.RECEIVED);
        OrderEntity entity2 = createOrderEntity(2L, OrderStatus.IN_PREPARATION);
        Page<OrderEntity> entityPage = new PageImpl<>(Arrays.asList(entity1, entity2), pageable, 2);

        Order order1 = createOrder(1L, OrderStatus.RECEIVED);
        Order order2 = createOrder(2L, OrderStatus.IN_PREPARATION);

        when(orderRepository.findAll(pageable)).thenReturn(entityPage);
        when(orderMapper.entityToDomain(entity1)).thenReturn(order1);
        when(orderMapper.entityToDomain(entity2)).thenReturn(order2);

        // When
        Page<Order> result = orderRepositoryOut.findAllByOrderStatus(statuses, pageable);

        // Then
        assertNotNull(result);
        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should find order by ID")
    void givenOrderId_whenFindingById_thenOrderShouldBeReturned() {
        // Given
        Long orderId = 1L;
        OrderEntity entity = createOrderEntity(orderId, OrderStatus.RECEIVED);
        Order order = createOrder(orderId, OrderStatus.RECEIVED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(entity));
        when(orderMapper.entityToDomain(entity)).thenReturn(order);

        // When
        Order result = orderRepositoryOut.findById(orderId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(OrderStatus.RECEIVED, result.getOrderStatus());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Should return null when order not found by ID")
    void givenNonExistentId_whenFindingById_thenNullShouldBeReturned() {
        // Given
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When
        Order result = orderRepositoryOut.findById(orderId);

        // Then
        assertNull(result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Should save order successfully")
    void givenOrder_whenSaving_thenOrderShouldBeSaved() {
        // Given
        Order order = createOrder(null, OrderStatus.SENT);
        OrderEntity entity = createOrderEntity(null, OrderStatus.SENT);
        OrderEntity savedEntity = createOrderEntity(1L, OrderStatus.SENT);
        Order savedOrder = createOrder(1L, OrderStatus.SENT);

        when(orderMapper.domainToEntity(order)).thenReturn(entity);
        when(orderRepository.save(entity)).thenReturn(savedEntity);
        when(orderMapper.entityToDomain(savedEntity)).thenReturn(savedOrder);

        // When
        Order result = orderRepositoryOut.save(order);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.SENT, result.getOrderStatus());
        verify(orderRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should delete order by ID")
    void givenOrderId_whenDeleting_thenOrderShouldBeDeleted() {
        // Given
        Long orderId = 1L;
        doNothing().when(orderRepository).deleteById(orderId);

        // When
        orderRepositoryOut.delete(orderId);

        // Then
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    @DisplayName("Should update order successfully")
    void givenOrder_whenUpdating_thenOrderShouldBeUpdated() {
        // Given
        Order order = createOrder(1L, OrderStatus.IN_PREPARATION);
        OrderEntity entity = createOrderEntity(1L, OrderStatus.IN_PREPARATION);

        when(orderMapper.domainToEntity(order)).thenReturn(entity);
        when(orderRepository.save(entity)).thenReturn(entity);
        when(orderMapper.entityToDomain(entity)).thenReturn(order);

        // When
        Order result = orderRepositoryOut.update(order);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.IN_PREPARATION, result.getOrderStatus());
        verify(orderRepository, times(1)).save(entity);
    }

    private Order createOrder(Long id, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setOrderStatus(status);
        return order;
    }

    private OrderEntity createOrderEntity(Long id, OrderStatus status) {
        OrderEntity entity = new OrderEntity();
        entity.setId(id);
        entity.setOrderStatus(status != null ? status.name() : null);
        return entity;
    }
}
