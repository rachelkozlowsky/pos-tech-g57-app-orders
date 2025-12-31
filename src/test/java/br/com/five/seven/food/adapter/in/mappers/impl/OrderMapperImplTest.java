package br.com.five.seven.food.adapter.in.mappers.impl;

import br.com.five.seven.food.adapter.in.mappers.ItemMapper;
import br.com.five.seven.food.adapter.in.payload.item.ItemRequest;
import br.com.five.seven.food.adapter.in.payload.item.ItemResponse;
import br.com.five.seven.food.adapter.in.payload.order.CreateOrderRequest;
import br.com.five.seven.food.adapter.in.payload.order.OrderMonitorResponse;
import br.com.five.seven.food.adapter.in.payload.order.OrderResponse;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderItemsRequest;
import br.com.five.seven.food.adapter.in.payload.order.UpdateOrderRequest;
import br.com.five.seven.food.adapter.out.relational.entity.ItemEntity;
import br.com.five.seven.food.adapter.out.relational.entity.OrderEntity;
import br.com.five.seven.food.application.domain.Item;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Mapper Tests")
class OrderMapperImplTest {

    @Mock
    private ItemMapper itemMapper;

    private OrderMapperImpl orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapperImpl(itemMapper);
    }

    @Test
    @DisplayName("Should map CreateOrderRequest to Order domain")
    void givenCreateOrderRequest_whenMappingToDomain_thenOrderShouldBeCreated() {
        // Given
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setTitle("Pedido 1");
        request.setDescription("Pedido de teste");
        request.setCpfClient("12345678900");
        request.setItems(List.of(itemRequest));

        Item item = createItem(null, 2);
        when(itemMapper.requestListToDomainList(anyList())).thenReturn(List.of(item));

        // When
        Order result = orderMapper.createRequestToDomain(request);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Pedido 1", result.getTitle());
        assertEquals("Pedido de teste", result.getDescription());
        assertEquals(OrderStatus.CREATED, result.getOrderStatus());
        assertEquals("12345678900", result.getCpfClient());
        assertEquals(1, result.getItems().size());
        verify(itemMapper, times(1)).requestListToDomainList(anyList());
    }

    @Test
    @DisplayName("Should map UpdateOrderRequest to Order domain")
    void givenUpdateOrderRequest_whenMappingToDomain_thenOrderShouldBeCreated() {
        // Given
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(3);

        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setTitle("Pedido Atualizado");
        request.setDescription("Descrição atualizada");
        request.setOrderStatus(OrderStatus.IN_PREPARATION);
        request.setCpfClient("98765432100");
        request.setItems(List.of(itemRequest));

        Item item = createItem(null, 3);
        when(itemMapper.requestListToDomainList(anyList())).thenReturn(List.of(item));

        // When
        Order result = orderMapper.updateRequestToDomain(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pedido Atualizado", result.getTitle());
        assertEquals("Descrição atualizada", result.getDescription());
        assertEquals(OrderStatus.IN_PREPARATION, result.getOrderStatus());
        assertEquals("98765432100", result.getCpfClient());
        assertEquals(1, result.getItems().size());
        verify(itemMapper, times(1)).requestListToDomainList(anyList());
    }

    @Test
    @DisplayName("Should map Order domain to OrderResponse")
    void givenOrderDomain_whenMappingToResponse_thenOrderResponseShouldBeCreated() {
        // Given
        Order order = createOrder(1L, OrderStatus.RECEIVED);
        ItemResponse itemResponse = new ItemResponse(null, 2);
        when(itemMapper.domainListToResponseList(anyList())).thenReturn(List.of(itemResponse));

        // When
        OrderResponse result = orderMapper.domainToResponse(order);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pedido 1", result.getTitle());
        assertEquals(OrderStatus.RECEIVED, result.getOrderStatus());
        assertNotNull(result.getItems());
        verify(itemMapper, times(1)).domainListToResponseList(anyList());
    }

    @Test
    @DisplayName("Should map Order domain to OrderEntity")
    void givenOrderDomain_whenMappingToEntity_thenOrderEntityShouldBeCreated() {
        // Given
        Order order = createOrder(1L, OrderStatus.RECEIVED);
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setQuantity(2);
        when(itemMapper.domainListToEntityList(anyList())).thenReturn(List.of(itemEntity));

        // When
        OrderEntity result = orderMapper.domainToEntity(order);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pedido 1", result.getTitle());
        assertEquals("RECEIVED", result.getOrderStatus());
        assertNotNull(result.getItems());
        verify(itemMapper, times(1)).domainListToEntityList(anyList());
    }

    @Test
    @DisplayName("Should map OrderEntity to Order domain")
    void givenOrderEntity_whenMappingToDomain_thenOrderShouldBeCreated() {
        // Given
        OrderEntity orderEntity = createOrderEntity(1L, OrderStatus.RECEIVED);
        Item item = createItem(1L, 2);
        when(itemMapper.entityListToDomainList(anyList())).thenReturn(List.of(item));

        // When
        Order result = orderMapper.entityToDomain(orderEntity);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Pedido 1", result.getTitle());
        assertEquals(OrderStatus.RECEIVED, result.getOrderStatus());
        assertNotNull(result.getItems());
        verify(itemMapper, times(1)).entityListToDomainList(anyList());
    }

    @Test
    @DisplayName("Should map Order domain to OrderMonitorResponse")
    void givenOrderDomain_whenMappingToMonitorResponse_thenOrderMonitorResponseShouldBeCreated() {
        // Given
        Order order = createOrder(1L, OrderStatus.IN_PREPARATION);

        // When
        OrderMonitorResponse result = orderMapper.domainToMonitorResponse(order);

        // Then
        assertNotNull(result);
        assertEquals("Pedido 1", result.getTitle());
        assertEquals(OrderStatus.IN_PREPARATION, result.getOrderStatus());
        assertEquals("12345678900", result.getClientCpf());
    }

    @Test
    @DisplayName("Should map UpdateOrderItemsRequest to Order domain")
    void givenUpdateOrderItemsRequest_whenMappingToDomain_thenOrderShouldBeCreated() {
        // Given
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setProductId(2L);
        itemRequest.setQuantity(5);

        UpdateOrderItemsRequest request = new UpdateOrderItemsRequest();
        request.setItems(List.of(itemRequest));

        Item item = createItem(null, 5);
        when(itemMapper.requestListToDomainList(anyList())).thenReturn(List.of(item));

        // When
        Order result = orderMapper.updateOrderItemsRequestToDomain(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getTitle());
        assertNull(result.getDescription());
        assertEquals(1, result.getItems().size());
        verify(itemMapper, times(1)).requestListToDomainList(anyList());
    }

    private Order createOrder(Long id, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setTitle("Pedido 1");
        order.setDescription("Descrição do pedido");
        order.setOrderStatus(status);
        order.setCpfClient("12345678900");
        order.setItems(new ArrayList<>());
        order.setTotalAmount(BigDecimal.valueOf(50.00));
        order.setReceivedAt(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setRemainingTime("20 minutos");

        Item item = createItem(1L, 2);
        item.setOrder(order);
        order.getItems().add(item);

        return order;
    }

    private OrderEntity createOrderEntity(Long id, OrderStatus status) {
        OrderEntity entity = new OrderEntity();
        entity.setId(id);
        entity.setTitle("Pedido 1");
        entity.setDescription("Descrição do pedido");
        entity.setOrderStatus(status.name());
        entity.setCpfClient("12345678900");
        entity.setItems(new ArrayList<>());
        entity.setTotalAmount(BigDecimal.valueOf(50.00));
        entity.setReceivedAt(LocalDateTime.now());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setRemainingTime("20 minutos");

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1L);
        itemEntity.setQuantity(2);
        itemEntity.setOrder(entity);
        entity.getItems().add(itemEntity);

        return entity;
    }

    private Item createItem(Long id, int quantity) {
        Product product = new Product();
        product.setId(1L);
        product.setName("Hambúrguer");
        product.setPrice(BigDecimal.valueOf(25.00));

        Item item = new Item();
        item.setId(id);
        item.setProduct(product);
        item.setQuantity(quantity);
        return item;
    }
}
