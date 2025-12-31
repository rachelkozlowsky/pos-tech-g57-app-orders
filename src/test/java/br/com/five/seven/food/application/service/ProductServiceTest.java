package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("Product Service BDD Tests")
class ProductServiceTest {

    @Mock
    private IProductRepositoryOut productRepository;

    @InjectMocks
    private ProductService productService;

    // CREATE PRODUCT TESTS

    @Test
    @DisplayName("Scenario: Successfully create a new product with valid data")
    void givenValidProduct_whenCreatingProduct_thenProductShouldBeCreated() throws ValidationException {
        // Given: A valid product with all required fields
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");

        Product product = new Product();
        product.setId(1L);
        product.setName("Hambúrguer");
        product.setDescription("Delicioso hambúrguer");
        product.setPrice(BigDecimal.valueOf(25.90));
        product.setCategory(category);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When: Creating the product
        Product result = productService.createProduct(product);

        // Then: The product should be created successfully
        assertNotNull(result, "Created product should not be null");
        assertEquals("Hambúrguer", result.getName(), "Product name should match");
        assertEquals(BigDecimal.valueOf(25.90), result.getPrice(), "Product price should match");
        assertEquals(1L, result.getId(), "Product ID should match");
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Scenario: Fail to create product when name is null")
    void givenProductWithNullName_whenCreatingProduct_thenValidationExceptionShouldBeThrown() {
        // Given: A product with null name
        Category category = new Category();
        category.setId(1L);

        Product product = new Product();
        product.setName(null);
        product.setPrice(BigDecimal.valueOf(25.90));
        product.setCategory(category);

        // When & Then: Creating the product should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> productService.createProduct(product),
            "Should throw ValidationException when name is null"
        );

        assertEquals("Product name cannot be empty", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create product when price is null")
    void givenProductWithNullPrice_whenCreatingProduct_thenValidationExceptionShouldBeThrown() {
        // Given: A product with null price
        Category category = new Category();
        category.setId(1L);

        Product product = new Product();
        product.setName("Hambúrguer");
        product.setPrice(null);
        product.setCategory(category);

        // When & Then: Creating the product should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> productService.createProduct(product),
            "Should throw ValidationException when price is null"
        );

        assertEquals("Product price cannot be empty or zero", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create product when price is zero")
    void givenProductWithZeroPrice_whenCreatingProduct_thenValidationExceptionShouldBeThrown() {
        // Given: A product with zero price
        Category category = new Category();
        category.setId(1L);

        Product product = new Product();
        product.setName("Hambúrguer");
        product.setPrice(BigDecimal.ZERO);
        product.setCategory(category);

        // When & Then: Creating the product should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> productService.createProduct(product),
            "Should throw ValidationException when price is zero"
        );

        assertEquals("Product price cannot be empty or zero", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create product when category is null")
    void givenProductWithNullCategory_whenCreatingProduct_thenValidationExceptionShouldBeThrown() {
        // Given: A product without category
        Product product = new Product();
        product.setName("Hambúrguer");
        product.setPrice(BigDecimal.valueOf(25.90));
        product.setCategory(null);

        // When & Then: Creating the product should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> productService.createProduct(product),
            "Should throw ValidationException when category is null"
        );

        assertEquals("Product category cannot be empty", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    // RETRIEVE PRODUCT TESTS

    @Test
    @DisplayName("Scenario: Successfully retrieve product by ID")
    void givenExistingProductId_whenGettingProductById_thenProductShouldBeReturned() {
        // Given: An existing product in the repository
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");

        Product product = new Product();
        product.setId(1L);
        product.setName("Hambúrguer");
        product.setPrice(BigDecimal.valueOf(25.90));
        product.setCategory(category);

        when(productRepository.getById(1L)).thenReturn(product);

        // When: Getting the product by ID
        Product result = productService.getProductById(1L);

        // Then: The product should be returned
        assertNotNull(result, "Retrieved product should not be null");
        assertEquals(1L, result.getId(), "Product ID should match");
        assertEquals("Hambúrguer", result.getName(), "Product name should match");
        verify(productRepository, times(1)).getById(1L);
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve all products")
    void givenMultipleProducts_whenGettingAllProducts_thenAllProductsShouldBeReturned() {
        // Given: Multiple products exist in the repository
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Hambúrguer");
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Pizza");
        product2.setCategory(category);

        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.getAll()).thenReturn(products);

        // When: Getting all products
        List<Product> result = productService.getAllProducts();

        // Then: All products should be returned
        assertNotNull(result, "Retrieved products should not be null");
        assertEquals(2, result.size(), "Should return all products");
        verify(productRepository, times(1)).getAll();
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve products by category")
    void givenProductsInCategory_whenGettingProductsByCategory_thenCategoryProductsShouldBeReturned() {
        // Given: Products exist in a specific category
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");

        Product product = new Product();
        product.setId(1L);
        product.setName("Hambúrguer");
        product.setCategory(category);

        List<Product> products = List.of(product);
        when(productRepository.getByCategory("Lanches")).thenReturn(products);

        // When: Getting products by category name
        List<Product> result = productService.getProductsByCategory("Lanches");

        // Then: Products from that category should be returned
        assertNotNull(result, "Retrieved products should not be null");
        assertEquals(1, result.size(), "Should return products from category");
        assertEquals("Hambúrguer", result.get(0).getName(), "Product name should match");
        verify(productRepository, times(1)).getByCategory("Lanches");
    }

    // UPDATE PRODUCT TESTS

    @Test
    @DisplayName("Scenario: Successfully update existing product")
    void givenExistingProduct_whenUpdatingProduct_thenProductShouldBeUpdated() throws ValidationException {
        // Given: An existing product and updated data
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");

        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Hambúrguer");
        existingProduct.setPrice(BigDecimal.valueOf(25.90));
        existingProduct.setCategory(category);

        Product updatedProduct = new Product();
        updatedProduct.setName("Hambúrguer Especial");
        updatedProduct.setDescription("Hambúrguer especial da casa");
        updatedProduct.setPrice(BigDecimal.valueOf(35.90));
        updatedProduct.setCategory(category);

        when(productRepository.getById(1L)).thenReturn(existingProduct);
        when(productRepository.update(any(Product.class))).thenReturn(updatedProduct);

        // When: Updating the product
        Product result = productService.updateProduct(1L, updatedProduct);

        // Then: The product should be updated
        assertNotNull(result, "Updated product should not be null");
        verify(productRepository, times(1)).getById(1L);
        verify(productRepository, times(1)).update(updatedProduct);
    }

    // DELETE PRODUCT TESTS

    @Test
    @DisplayName("Scenario: Successfully delete existing product")
    void givenExistingProduct_whenDeletingProduct_thenProductShouldBeDeleted() {
        // Given: An existing product
        Category category = new Category();
        category.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("Hambúrguer");
        product.setCategory(category);

        when(productRepository.getById(1L)).thenReturn(product);
        doNothing().when(productRepository).delete(1L);

        // When: Deleting the product
        productService.deleteProduct(1L);

        // Then: The product should be deleted
        verify(productRepository, times(1)).getById(1L);
        verify(productRepository, times(1)).delete(1L);
    }
}
