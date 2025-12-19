package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private IProductRepositoryOut productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");
        
        product = new Product();
        product.setId(1L);
        product.setName("Hambúrguer");
        product.setDescription("Delicioso hambúrguer");
        product.setPrice(BigDecimal.valueOf(25.90));
        product.setCategory(category);
    }

    @Test
    void shouldCreateProductSuccessfully() throws ValidationException {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals("Hambúrguer", result.getName());
        assertEquals(BigDecimal.valueOf(25.90), result.getPrice());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void shouldThrowExceptionWhenProductNameIsNull() {
        product.setName(null);

        assertThrows(ValidationException.class, () -> productService.createProduct(product));
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenProductPriceIsNull() {
        product.setPrice(null);

        assertThrows(ValidationException.class, () -> productService.createProduct(product));
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenProductPriceIsZero() {
        product.setPrice(BigDecimal.ZERO);

        assertThrows(ValidationException.class, () -> productService.createProduct(product));
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldGetProductById() {
        when(productRepository.getById(1L)).thenReturn(product);

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Hambúrguer", result.getName());
        verify(productRepository, times(1)).getById(1L);
    }

    @Test
    void shouldGetAllProducts() {
        List<Product> products = Arrays.asList(product, new Product());
        when(productRepository.getAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).getAll();
    }

    @Test
    void shouldGetProductsByCategory() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.getByCategory("Lanches")).thenReturn(products);

        List<Product> result = productService.getProductsByCategory("Lanches");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).getByCategory("Lanches");
    }

    @Test
    void shouldUpdateProductSuccessfully() throws ValidationException {
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");
        
        Product updatedProduct = new Product();
        updatedProduct.setName("Hambúrguer Especial");
        updatedProduct.setDescription("Hambúrguer especial da casa");
        updatedProduct.setPrice(BigDecimal.valueOf(35.90));
        updatedProduct.setCategory(category);

        when(productRepository.getById(1L)).thenReturn(product);
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updatedProduct);

        assertNotNull(result);
        verify(productRepository, times(1)).getById(1L);
        verify(productRepository, times(1)).update(updatedProduct);
    }

    @Test
    void shouldDeleteProduct() {
        when(productRepository.getById(1L)).thenReturn(product);
        doNothing().when(productRepository).delete(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).getById(1L);
        verify(productRepository, times(1)).delete(1L);
    }
}
