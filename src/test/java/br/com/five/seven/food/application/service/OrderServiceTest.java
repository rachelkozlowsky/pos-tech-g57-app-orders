package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Item;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import br.com.five.seven.food.application.ports.in.CategoryServiceIn;
import br.com.five.seven.food.application.ports.out.IClientApiOut;
import br.com.five.seven.food.application.ports.out.IOrderRepositoryOut;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    private Order order;
    private Product product;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.RECEIVED);
        order.setItems(new ArrayList<>());

        product = new Product();
        product.setId(1L);
        product.setName("Hambúrguer");
        product.setPrice(BigDecimal.valueOf(25.90));

        Item item = new Item();
        item.setProduct(product);
        item.setQuantity(2);
        order.getItems().add(item);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void shouldFindAllOrders() {
        List<Order> orders = Arrays.asList(order);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);

        Page<Order> result = orderService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldFindAllByOrderStatus() {
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.RECEIVED);
        List<Order> orders = Arrays.asList(order);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        
        when(orderRepository.findAllByOrderStatus(anyList(), any(Pageable.class))).thenReturn(orderPage);

        Page<Order> result = orderService.findAllByOrderStatus(statuses, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findAllByOrderStatus(statuses, pageable);
    }

    @Test
    void shouldFindOrderById() {
        when(orderRepository.findById(1L)).thenReturn(order);

        Order result = orderService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(OrderStatus.RECEIVED, result.getOrderStatus());
        verify(orderRepository, times(1)).findById(1L);
    }
}
