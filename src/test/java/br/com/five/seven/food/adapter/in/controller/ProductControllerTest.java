package br.com.five.seven.food.adapter.in.controller;

import br.com.five.seven.food.adapter.in.payload.products.ProductRequest;
import br.com.five.seven.food.adapter.in.payload.products.ProductResponse;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.ports.in.CategoryServiceIn;
import br.com.five.seven.food.application.ports.in.ProductServiceIn;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Controller BDD Tests")
class ProductControllerTest {

    @Mock
    private ProductServiceIn productService;

    @Mock
    private CategoryServiceIn categoryService;

    @InjectMocks
    private ProductController productController;

    // CREATE PRODUCT TESTS

    @Test
    @DisplayName("Scenario: Successfully create product")
    void givenValidProductRequest_whenCreatingProduct_thenProductShouldBeCreatedAndReturned() throws ValidationException {
        // Given: A valid product request
        ProductRequest request = new ProductRequest();
        request.setName("Hambúrguer");
        request.setDescription("Delicioso hambúrguer");
        request.setPrice(BigDecimal.valueOf(25.90));
        request.setCategory("Lanches");
        request.setImages(new ArrayList<>());

        Category category = createCategory(1L, "Lanches");
        Product product = createProduct(1L, "Hambúrguer", BigDecimal.valueOf(25.90), category);

        when(categoryService.getCategoryByName("Lanches")).thenReturn(category);
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        // When: Creating product
        ResponseEntity<ProductResponse> response = productController.createProduct(request);

        // Then: Product should be created
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Hambúrguer", response.getBody().getName());
        assertEquals(0, BigDecimal.valueOf(25.90).compareTo(response.getBody().getPrice()));
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("Scenario: Fail to create product with non-existing category")
    void givenProductRequestWithInvalidCategory_whenCreatingProduct_thenBadRequestShouldBeReturned() throws ValidationException {
        // Given: A product request with non-existing category
        ProductRequest request = new ProductRequest();
        request.setName("Hambúrguer");
        request.setPrice(BigDecimal.valueOf(25.90));
        request.setCategory("NonExisting");
        request.setImages(new ArrayList<>());

        when(categoryService.getCategoryByName("NonExisting"))
                .thenThrow(new NoSuchElementException("Category not found"));

        // When: Creating product
        ResponseEntity<ProductResponse> response = productController.createProduct(request);

        // Then: Bad request should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(productService, never()).createProduct(any());
    }

    @Test
    @DisplayName("Scenario: Successfully create product with zero price")
    void givenProductWithZeroPrice_whenCreatingProduct_thenProductShouldBeCreated() throws ValidationException {
        // Given: A product with zero price (free item)
        ProductRequest request = new ProductRequest();
        request.setName("Item Grátis");
        request.setDescription("Promoção");
        request.setPrice(BigDecimal.ZERO);
        request.setCategory("Lanches");
        request.setImages(new ArrayList<>());

        Category category = createCategory(1L, "Lanches");
        Product product = createProduct(1L, "Item Grátis", BigDecimal.ZERO, category);

        when(categoryService.getCategoryByName("Lanches")).thenReturn(category);
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        // When: Creating product
        ResponseEntity<ProductResponse> response = productController.createProduct(request);

        // Then: Product should be created
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getBody().getPrice()));
    }

    @Test
    @DisplayName("Scenario: Successfully create inactive product")
    void givenInactiveProductRequest_whenCreatingProduct_thenInactiveProductShouldBeCreated() throws ValidationException {
        // Given: An inactive product request
        ProductRequest request = new ProductRequest();
        request.setName("Produto Descontinuado");
        request.setPrice(BigDecimal.valueOf(15.90));
        request.setCategory("Lanches");
        request.setActive(false);
        request.setImages(new ArrayList<>());

        Category category = createCategory(1L, "Lanches");
        Product product = createProduct(1L, "Produto Descontinuado", BigDecimal.valueOf(15.90), category);
        product.setActive(false);

        when(categoryService.getCategoryByName("Lanches")).thenReturn(category);
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        // When: Creating product
        ResponseEntity<ProductResponse> response = productController.createProduct(request);

        // Then: Inactive product should be created
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isActive());
    }

    @Test
    @DisplayName("Scenario: Fail to create product with negative price")
    void givenProductWithNegativePrice_whenCreatingProduct_thenExceptionShouldBeThrown() throws ValidationException {
        // Given: A product with negative price
        ProductRequest request = new ProductRequest();
        request.setName("Invalid Product");
        request.setPrice(BigDecimal.valueOf(-10.00));
        request.setCategory("Lanches");
        request.setImages(new ArrayList<>());

        Category category = createCategory(1L, "Lanches");

        when(categoryService.getCategoryByName("Lanches")).thenReturn(category);
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new ValidationException("Price cannot be negative"));

        // When & Then: Exception should be thrown
        assertThrows(ValidationException.class, () -> productController.createProduct(request));
    }

    // RETRIEVE PRODUCT TESTS

    @Test
    @DisplayName("Scenario: Successfully retrieve product by ID")
    void givenExistingProductId_whenGettingProductById_thenProductShouldBeReturned() {
        // Given: An existing product
        Category category = createCategory(1L, "Lanches");
        Product product = createProduct(1L, "Hambúrguer", BigDecimal.valueOf(25.90), category);

        when(productService.getProductById(1L)).thenReturn(product);

        // When: Getting product by ID
        ResponseEntity<ProductResponse> response = productController.getProductById(1L);

        // Then: Product should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Hambúrguer", response.getBody().getName());
        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("Scenario: Return 404 when product not found by ID")
    void givenNonExistingProductId_whenGettingProductById_thenNotFoundShouldBeReturned() {
        // Given: A non-existing product ID
        when(productService.getProductById(999L)).thenReturn(null);

        // When: Getting product by ID
        ResponseEntity<ProductResponse> response = productController.getProductById(999L);

        // Then: 404 should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve all products")
    void givenMultipleProducts_whenGettingAllProducts_thenAllProductsShouldBeReturned() {
        // Given: Multiple products exist
        Category category = createCategory(1L, "Lanches");
        Product product1 = createProduct(1L, "Hambúrguer", BigDecimal.valueOf(25.90), category);
        Product product2 = createProduct(2L, "Pizza", BigDecimal.valueOf(35.90), category);

        List<Product> products = Arrays.asList(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        // When: Getting all products
        ResponseEntity<List<ProductResponse>> response = productController.getAllProducts();

        // Then: All products should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Scenario: Return empty list when no products exist")
    void givenNoProducts_whenGettingAllProducts_thenEmptyListShouldBeReturned() {
        // Given: No products exist
        when(productService.getAllProducts()).thenReturn(List.of());

        // When: Getting all products
        ResponseEntity<List<ProductResponse>> response = productController.getAllProducts();

        // Then: Empty list should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve products by category")
    void givenProductsInCategory_whenGettingProductsByCategory_thenCategoryProductsShouldBeReturned() {
        // Given: Products exist in a specific category
        Category category = createCategory(1L, "Lanches");
        Product product = createProduct(1L, "Hambúrguer", BigDecimal.valueOf(25.90), category);

        List<Product> products = List.of(product);
        when(productService.getProductsByCategory("Lanches")).thenReturn(products);

        // When: Getting products by category
        ResponseEntity<List<ProductResponse>> response = productController.getProductsByCategory("Lanches");

        // Then: Category products should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Hambúrguer", response.getBody().get(0).getName());
        verify(productService, times(1)).getProductsByCategory("Lanches");
    }

    @Test
    @DisplayName("Scenario: Return empty list when no products in category")
    void givenCategoryWithoutProducts_whenGettingProductsByCategory_thenEmptyListShouldBeReturned() {
        // Given: A category without products
        when(productService.getProductsByCategory("EmptyCategory")).thenReturn(List.of());

        // When: Getting products by category
        ResponseEntity<List<ProductResponse>> response = productController.getProductsByCategory("EmptyCategory");

        // Then: Empty list should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve products from multiple categories")
    void givenMultipleCategories_whenGettingProductsByEachCategory_thenCorrectProductsShouldBeReturned() {
        // Given: Products in different categories
        Category category1 = createCategory(1L, "Lanches");
        Category category2 = createCategory(2L, "Bebidas");

        Product product1 = createProduct(1L, "Hambúrguer", BigDecimal.valueOf(25.90), category1);
        Product product2 = createProduct(2L, "Refrigerante", BigDecimal.valueOf(5.90), category2);

        when(productService.getProductsByCategory("Lanches")).thenReturn(List.of(product1));
        when(productService.getProductsByCategory("Bebidas")).thenReturn(List.of(product2));

        // When: Getting products from each category
        ResponseEntity<List<ProductResponse>> responseLanches = productController.getProductsByCategory("Lanches");
        ResponseEntity<List<ProductResponse>> responseBebidas = productController.getProductsByCategory("Bebidas");

        // Then: Correct products should be returned for each category
        assertEquals(1, responseLanches.getBody().size());
        assertEquals("Hambúrguer", responseLanches.getBody().get(0).getName());

        assertEquals(1, responseBebidas.getBody().size());
        assertEquals("Refrigerante", responseBebidas.getBody().get(0).getName());
    }

    // UPDATE PRODUCT TESTS

    @Test
    @DisplayName("Scenario: Successfully update product")
    void givenExistingProduct_whenUpdatingProduct_thenProductShouldBeUpdated() throws ValidationException {
        // Given: An existing product and valid update request
        ProductRequest request = new ProductRequest();
        request.setName("Hambúrguer Especial");
        request.setDescription("Hambúrguer especial da casa");
        request.setPrice(BigDecimal.valueOf(35.90));
        request.setCategory("Lanches");
        request.setImages(new ArrayList<>());

        Category category = createCategory(1L, "Lanches");
        Product updatedProduct = createProduct(1L, "Hambúrguer Especial", BigDecimal.valueOf(35.90), category);

        when(categoryService.getCategoryByName("Lanches")).thenReturn(category);
        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(updatedProduct);

        // When: Updating product
        ResponseEntity<ProductResponse> response = productController.updateProduct(1L, request);

        // Then: Product should be updated
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hambúrguer Especial", response.getBody().getName());
        assertEquals(0, BigDecimal.valueOf(35.90).compareTo(response.getBody().getPrice()));
        verify(productService, times(1)).updateProduct(anyLong(), any(Product.class));
    }

    @Test
    @DisplayName("Scenario: Successfully update product category")
    void givenProductWithNewCategory_whenUpdatingProduct_thenCategoryShouldBeChanged() throws ValidationException {
        // Given: A product being moved to a different category
        ProductRequest request = new ProductRequest();
        request.setName("Hambúrguer");
        request.setPrice(BigDecimal.valueOf(25.90));
        request.setCategory("Bebidas");
        request.setImages(new ArrayList<>());

        Category newCategory = createCategory(2L, "Bebidas");
        Product updatedProduct = createProduct(1L, "Hambúrguer", BigDecimal.valueOf(25.90), newCategory);

        when(categoryService.getCategoryByName("Bebidas")).thenReturn(newCategory);
        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(updatedProduct);

        // When: Updating product
        ResponseEntity<ProductResponse> response = productController.updateProduct(1L, request);

        // Then: Category should be changed
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bebidas", response.getBody().getCategory().getName());
    }

    @Test
    @DisplayName("Scenario: Fail to update non-existing product")
    void givenNonExistingProductId_whenUpdatingProduct_thenExceptionShouldBeThrown() throws ValidationException {
        // Given: A non-existing product ID
        ProductRequest request = new ProductRequest();
        request.setName("Product");
        request.setPrice(BigDecimal.valueOf(10.00));
        request.setCategory("Lanches");
        request.setImages(new ArrayList<>());

        Category category = createCategory(1L, "Lanches");

        when(categoryService.getCategoryByName("Lanches")).thenReturn(category);
        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new RuntimeException("Product not found"));

        // When & Then: Exception should be thrown
        assertThrows(RuntimeException.class, () -> productController.updateProduct(999L, request));
    }

    // DELETE PRODUCT TESTS

    @Test
    @DisplayName("Scenario: Successfully delete product")
    void givenExistingProduct_whenDeletingProduct_thenProductShouldBeDeleted() {
        // Given: An existing product
        doNothing().when(productService).deleteProduct(1L);

        // When: Deleting product
        ResponseEntity<Void> response = productController.deleteProduct(1L);

        // Then: Product should be deleted
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Scenario: Fail to delete non-existing product")
    void givenNonExistingProduct_whenDeletingProduct_thenExceptionShouldBeThrown() {
        // Given: A non-existing product
        doThrow(new RuntimeException("Product not found")).when(productService).deleteProduct(999L);

        // When & Then: Exception should be thrown
        assertThrows(RuntimeException.class, () -> productController.deleteProduct(999L));
    }

    // Helper methods
    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    private Product createProduct(Long id, String name, BigDecimal price, Category category) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);
        product.setActive(true);
        return product;
    }
}

