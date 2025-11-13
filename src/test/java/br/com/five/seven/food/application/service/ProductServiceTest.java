package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private final IProductRepositoryOut IProductRepositoryOut = mock(IProductRepositoryOut.class);
    private final ProductService productService = new ProductService(IProductRepositoryOut);

    @Test
    public void test_create_product_returns_saved_product() throws ValidationException {
        Category category = new Category();
        category.setName("Category");
        Product product = new Product();
        product.setName("Burger");
        product.setDescription("Delicious burger");
        product.setPrice(new BigDecimal("10.99"));
        product.setActive(true);
        product.setCategory(category);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Burger");
        savedProduct.setDescription("Delicious burger");
        savedProduct.setPrice(new BigDecimal("10.99"));
        savedProduct.setActive(true);
        product.setCategory(category);

        when(IProductRepositoryOut.save(product)).thenReturn(savedProduct);

        Product result = productService.createProduct(product);

        assertEquals(1L, result.getId());
        assertEquals("Burger", result.getName());
        verify(IProductRepositoryOut).save(product);
    }


    @Test
    public void test_get_product_by_id_returns_correct_product() {

        Long productId = 123L;
        Product expectedProduct = new Product();
        expectedProduct.setId(productId);
        expectedProduct.setName("Pizza");
        expectedProduct.setDescription("Cheese pizza");
        expectedProduct.setPrice(new BigDecimal("15.99"));

        when(IProductRepositoryOut.getById(productId)).thenReturn(expectedProduct);

        Product result = productService.getProductById(productId);

        assertEquals(productId, result.getId());
        assertEquals("Pizza", result.getName());
        assertEquals("Cheese pizza", result.getDescription());
        assertEquals(new BigDecimal("15.99"), result.getPrice());
        verify(IProductRepositoryOut).getById(productId);
    }


    @Test
    public void test_get_all_products_returns_list_of_all_products() {

        List<Product> expectedProducts = new ArrayList<>();

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Burger");
        expectedProducts.add(product1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Pizza");
        expectedProducts.add(product2);

        when(IProductRepositoryOut.getAll()).thenReturn(expectedProducts);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(IProductRepositoryOut).getAll();
    }

    @Test
    public void test_update_product_with_valid_data() throws ValidationException {
        Long productId = 123L;
        Category category = new Category();
        category.setName("category");
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Name");
        existingProduct.setDescription("Old Description");
        existingProduct.setPrice(BigDecimal.valueOf(10.0));
        existingProduct.setCategory(category);

        Product updatedProduct = new Product();
        updatedProduct.setName("New Name");
        updatedProduct.setDescription("New Description");
        updatedProduct.setPrice(BigDecimal.valueOf(15.0));
        updatedProduct.setCategory(category);

        IProductRepositoryOut productRepository = mock(IProductRepositoryOut.class);
        when(productRepository.getById(productId)).thenReturn(existingProduct);
        when(productRepository.update(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductService productService = new ProductService(productRepository);

        Product result = productService.updateProduct(productId, updatedProduct);

        assertEquals(productId, result.getId());
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertEquals(BigDecimal.valueOf(15.0), result.getPrice());

        verify(productRepository).getById(productId);
        verify(productRepository).update(updatedProduct);
    }

    @Test
    public void test_delete_product_with_valid_id() {
        Long productId = 123L;
        Product product = new Product();
        product.setId(productId);

        when(IProductRepositoryOut.getById(productId)).thenReturn(product);
        productService.deleteProduct(productId);
        verify(IProductRepositoryOut).delete(productId);
        verify(IProductRepositoryOut).getById(productId);
        assertEquals(productId, product.getId());

    }

}
