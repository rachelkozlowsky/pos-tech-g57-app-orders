package br.com.five.seven.food.adapter.in.controller;

import br.com.five.seven.food.adapter.in.payload.products.ImageRequest;
import br.com.five.seven.food.adapter.in.payload.products.ProductRequest;
import br.com.five.seven.food.adapter.in.payload.products.ProductResponse;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.ports.in.CategoryServiceIn;
import br.com.five.seven.food.application.ports.in.ProductServiceIn;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    private final ProductServiceIn productService = mock(ProductServiceIn.class);
    private final CategoryServiceIn categoryService = mock(CategoryServiceIn.class);

    private final ProductController productController = new ProductController(productService, categoryService);

    @Test
    public void test_create_product_with_valid_data_returns_ok() throws ValidationException {

        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Burger");
        productRequest.setDescription("Delicious burger");
        productRequest.setPrice(new BigDecimal("15.99"));
        productRequest.setActive(true);
        productRequest.setImages(List.of(new ImageRequest("image_url")));
        productRequest.setCategory("Fast Food");

        Category category = new Category(1L, "Fast Food", true);
        Product product = productRequest.toDomain(category);

        when(categoryService.getCategoryByName("Fast Food")).thenReturn(category);
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        ResponseEntity<ProductResponse> response = productController.createProduct(productRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(product.getName(), response.getBody().getName());
        assertEquals(product.getDescription(), response.getBody().getDescription());
        assertEquals(product.getPrice(), response.getBody().getPrice());
    }


    @Test
    public void test_get_product_by_valid_id_returns_ok() {

        Long productId = 1L;
        Category category = new Category(1L, "Fast Food", true);
        Product product = new Product();
        product.setId(productId);
        product.setName("Burger");
        product.setDescription("Delicious burger");
        product.setPrice(new BigDecimal("15.99"));
        product.setActive(true);
        product.setCategory(category);

        when(productService.getProductById(productId)).thenReturn(product);

        ResponseEntity<ProductResponse> response = productController.getProductById(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getId());
        assertEquals(product.getName(), response.getBody().getName());
        assertEquals(category.getName(), response.getBody().getCategory().getName());

    }

    @Test
    public void test_get_all_products_returns_ok_with_list() {
        Category category = new Category(1L, "Fast Food", true);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Burger");
        product1.setCategory(category);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Pizza");
        product2.setCategory(category);

        List<Product> products = List.of(product1, product2);

        when(productService.getAllProducts()).thenReturn(products);

        ResponseEntity<List<ProductResponse>> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(product1.getId(), response.getBody().get(0).getId());
        assertEquals(product2.getId(), response.getBody().get(1).getId());
    }

    @Test
    public void test_create_product_with_nonexistent_category_returns_bad_request() throws ValidationException {

        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Burger");
        productRequest.setDescription("Delicious burger");
        productRequest.setPrice(new BigDecimal("15.99"));
        productRequest.setActive(true);
        productRequest.setImages(List.of(new ImageRequest("image_url")));
        productRequest.setCategory("Nonexistent Category");

        when(categoryService.getCategoryByName("Nonexistent Category")).thenThrow(new NoSuchElementException());

        ResponseEntity<ProductResponse> response = productController.createProduct(productRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(productService, never()).createProduct(any(Product.class));


    }

    @Test
    public void test_get_product_with_nonexistent_id_returns_not_found() {

        Long nonExistentProductId = 1L;

        when(productService.getProductById(nonExistentProductId)).thenReturn(null);

        ResponseEntity<ProductResponse> response = productController.getProductById(nonExistentProductId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(productService).getProductById(nonExistentProductId);
    }

    @Test
    public void test_update_product_success() throws ValidationException {

        Long productId = 123L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Produto Teste");
        productRequest.setDescription("Descrição do Produto Teste");
        productRequest.setPrice(new BigDecimal("19.99"));
        productRequest.setCategory("Categoria Teste");
        productRequest.setImages(List.of(new ImageRequest("image_url")));

        Category category = new Category(1L, "Fast Food", true);
        Product product = new Product();
        product.setId(productId);
        product.setName("Burger");
        product.setDescription("Delicious burger");
        product.setPrice(new BigDecimal("15.99"));
        product.setActive(true);
        product.setCategory(category);

        when(productService.getProductById(productId)).thenReturn(product);
        when(categoryService.getCategoryByName("Categoria Teste")).thenReturn(category);
        when(productService.updateProduct(any(), any())).thenReturn(product);

        ResponseEntity<ProductResponse> response = productController.updateProduct(productId, productRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(product.getName(), response.getBody().getName());
        assertEquals(product.getDescription(), response.getBody().getDescription());
        assertEquals(product.getPrice(), response.getBody().getPrice());
    }

    @Test
    public void test_update_product_not_found() throws ValidationException {

        Long nonExistentProductId = 999L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Updated Product");
        productRequest.setDescription("Updated Description");
        productRequest.setPrice(new BigDecimal("19.99"));
        productRequest.setActive(true);
        productRequest.setImages(List.of(new ImageRequest()));
        productRequest.setCategory("Food");

        Category category = new Category(456L, "Food", true);

        when(categoryService.getCategoryByName("Food")).thenReturn(category);
        when(productService.updateProduct(eq(nonExistentProductId), any(Product.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productController.updateProduct(nonExistentProductId, productRequest);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found", exception.getReason());

        verify(categoryService).getCategoryByName("Food");
        verify(productService).updateProduct(eq(nonExistentProductId), any(Product.class));
    }

    @Test
    public void test_delete_product_success() {

        Long productId = 123L;
        productService.getProductById(productId);
        doNothing().when(productService).deleteProduct(productId);

        ResponseEntity<Void> response = productController.deleteProduct(productId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService).deleteProduct(productId);
    }
}
