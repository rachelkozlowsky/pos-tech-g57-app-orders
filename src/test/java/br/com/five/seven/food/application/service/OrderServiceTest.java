package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Combo;
import br.com.five.seven.food.application.domain.Item;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import br.com.five.seven.food.application.ports.out.IOrderRepositoryOut;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private IOrderRepositoryOut IOrderRepositoryOut;

    @Mock
    private IProductRepositoryOut productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void shouldFindAllOrders() {
        Order order = createTestOrder();
        Product product = createTestProduct();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

        when(productRepository.getById(anyLong())).thenReturn(product);
        when(IOrderRepositoryOut.findAll(pageable)).thenReturn(orderPage);

        Page<Order> result = orderService.findAll(pageable);

        assertNotNull(result);
        assertEquals(order, result.getContent().getFirst());
    }

    @Test
    public void shouldFindOrderById() throws ValidationException {
        Long id = 1L;
        Order order = createTestOrder();
        Product product = createTestProduct();
        order.setId(id);

        when(productRepository.getById(anyLong())).thenReturn(product);
        when(IOrderRepositoryOut.findById(id)).thenReturn(order);

        Order result = orderService.findById(id);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void shouldSaveNewOrder() throws ValidationException {
        Order order = createTestOrder();
        Product product = createTestProduct();

        when(productRepository.getById(anyLong())).thenReturn(product);
        when(IOrderRepositoryOut.save(order)).thenReturn(order);

        Order result = orderService.create(order);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void shouldUpdateOrder() throws ValidationException {
        Order order = createTestOrder();
        Product product = createTestProduct();

        when(IOrderRepositoryOut.findById(order.getId())).thenReturn(order);
        when(productRepository.getById(anyLong())).thenReturn(product);
        when(IOrderRepositoryOut.update(order)).thenReturn(order);

        Order result = orderService.update(order.getId(), order);

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
    }

    @Test
    public void shouldDeleteOrder() {
        Order order = createTestOrder();
        doNothing().when(IOrderRepositoryOut).delete(order.getId());
        assertDoesNotThrow(() -> orderService.deleteById(order.getId()));
    }

    private Order createTestOrder() {
        return new Order(
                1L,
                "title",
                "description",
                OrderStatus.IN_PREPARATION,
                "56040817011",
                createTestCombo(),
                BigDecimal.ONE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Combo createTestCombo() {
        Product product = createTestProduct();
        Item item = new Item(1L, product, 1);
        List<Item> itemList = List.of(item);
        return new Combo(1L, itemList, itemList, itemList, itemList);
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("name");
        product.setDescription("description");
        product.setPrice(BigDecimal.ONE);
        product.setActive(true);
        product.setImages(new ArrayList<>());
        product.setCategory(new Category(1L, "snack", true));

        return product;
    }
}
