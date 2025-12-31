package br.com.five.seven.food.adapter.in.mappers;

import br.com.five.seven.food.adapter.in.payload.products.ImageRequest;
import br.com.five.seven.food.adapter.in.payload.products.ProductRequest;
import br.com.five.seven.food.adapter.out.relational.entity.CategoryEntity;
import br.com.five.seven.food.adapter.out.relational.entity.ImageEntity;
import br.com.five.seven.food.adapter.out.relational.entity.ProductEntity;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Image;
import br.com.five.seven.food.application.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Mapper Tests")
class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapperImpl();
    }

    @Test
    @DisplayName("Should map ProductEntity to Product domain")
    void givenProductEntity_whenMappingToDomain_thenProductShouldBeCreated() {
        // Given
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName("Lanches");
        categoryEntity.setActive(true);

        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setUrl("http://example.com/image.jpg");

        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Hambúrguer");
        entity.setDescription("Delicioso hambúrguer");
        entity.setPrice(BigDecimal.valueOf(25.90));
        entity.setActive(true);
        entity.setCategory(categoryEntity);
        entity.setImages(Arrays.asList(imageEntity));

        // When
        Product result = productMapper.toDomain(entity);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Hambúrguer", result.getName());
        assertEquals("Delicioso hambúrguer", result.getDescription());
        assertEquals(BigDecimal.valueOf(25.90), result.getPrice());
        assertTrue(result.isActive());
        assertNotNull(result.getCategory());
        assertEquals(1L, result.getCategory().getId());
        assertNotNull(result.getImages());
        assertEquals(1, result.getImages().size());
    }

    @Test
    @DisplayName("Should map Product domain to ProductEntity")
    void givenProductDomain_whenMappingToEntity_thenProductEntityShouldBeCreated() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Bebidas");
        category.setActive(true);

        Image image = new Image();
        image.setUrl("http://example.com/drink.jpg");

        Product product = new Product();
        product.setId(1L);
        product.setName("Refrigerante");
        product.setDescription("Refrigerante gelado");
        product.setPrice(BigDecimal.valueOf(5.00));
        product.setActive(true);
        product.setCategory(category);
        product.setImages(Arrays.asList(image));

        // When
        ProductEntity result = productMapper.fromDomain(product);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Refrigerante", result.getName());
        assertEquals("Refrigerante gelado", result.getDescription());
        assertEquals(BigDecimal.valueOf(5.00), result.getPrice());
        assertTrue(result.getActive());
        assertNotNull(result.getCategory());
        assertEquals(1L, result.getCategory().getId());
        assertNotNull(result.getImages());
        assertEquals(1, result.getImages().size());
    }

    @Test
    @DisplayName("Should map ProductRequest to Product domain")
    void givenProductRequest_whenMappingToDomain_thenProductShouldBeCreated() {
        // Given
        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setUrl("http://example.com/new-product.jpg");

        ProductRequest request = new ProductRequest();
        request.setName("Batata Frita");
        request.setDescription("Batata crocante");
        request.setPrice(BigDecimal.valueOf(8.50));
        request.setActive(true);
        request.setImages(Arrays.asList(imageRequest));

        // When
        Product result = productMapper.requestToDomain(request);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Batata Frita", result.getName());
        assertEquals("Batata crocante", result.getDescription());
        assertEquals(BigDecimal.valueOf(8.50), result.getPrice());
        assertTrue(result.isActive());
        assertNull(result.getCategory());
        assertNotNull(result.getImages());
        assertEquals(1, result.getImages().size());
    }

    @Test
    @DisplayName("Should return null when ProductEntity is null")
    void givenNullProductEntity_whenMappingToDomain_thenNullShouldBeReturned() {
        // When
        Product result = productMapper.toDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when Product domain is null")
    void givenNullProduct_whenMappingToEntity_thenNullShouldBeReturned() {
        // When
        ProductEntity result = productMapper.fromDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when ProductRequest is null")
    void givenNullProductRequest_whenMappingToDomain_thenNullShouldBeReturned() {
        // When
        Product result = productMapper.requestToDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle inactive product mapping")
    void givenInactiveProduct_whenMapping_thenInactiveFlagShouldBeMaintained() {
        // Given
        Product product = new Product();
        product.setId(2L);
        product.setName("Produto Inativo");
        product.setPrice(BigDecimal.valueOf(10.00));
        product.setActive(false);

        // When
        ProductEntity result = productMapper.fromDomain(product);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertFalse(result.getActive());
    }

    @Test
    @DisplayName("Should handle product without images")
    void givenProductWithoutImages_whenMapping_thenImagesShouldBeNull() {
        // Given
        Product product = new Product();
        product.setId(3L);
        product.setName("Produto Sem Imagem");
        product.setPrice(BigDecimal.valueOf(15.00));
        product.setActive(true);
        product.setImages(null);

        // When
        ProductEntity result = productMapper.fromDomain(product);

        // Then
        assertNotNull(result);
        assertNull(result.getImages());
    }

    @Test
    @DisplayName("Should handle product without category")
    void givenProductWithoutCategory_whenMapping_thenCategoryShouldBeNull() {
        // Given
        Product product = new Product();
        product.setId(4L);
        product.setName("Produto Sem Categoria");
        product.setPrice(BigDecimal.valueOf(20.00));
        product.setActive(true);
        product.setCategory(null);

        // When
        ProductEntity result = productMapper.fromDomain(product);

        // Then
        assertNotNull(result);
        assertNull(result.getCategory());
    }

    @Test
    @DisplayName("Should handle entity with empty images list")
    void givenProductEntityWithEmptyImagesList_whenMapping_thenEmptyListShouldBeReturned() {
        // Given
        ProductEntity entity = new ProductEntity();
        entity.setId(5L);
        entity.setName("Produto com lista vazia");
        entity.setPrice(BigDecimal.valueOf(12.00));
        entity.setActive(true);
        entity.setImages(Arrays.asList());

        // When
        Product result = productMapper.toDomain(entity);

        // Then
        assertNotNull(result);
        assertNotNull(result.getImages());
        assertEquals(0, result.getImages().size());
    }

    @Test
    @DisplayName("Should handle product request with empty images list")
    void givenProductRequestWithEmptyImagesList_whenMapping_thenEmptyListShouldBeReturned() {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("Produto Request");
        request.setPrice(BigDecimal.valueOf(15.00));
        request.setActive(true);
        request.setImages(Arrays.asList());

        // When
        Product result = productMapper.requestToDomain(request);

        // Then
        assertNotNull(result);
        assertNotNull(result.getImages());
        assertEquals(0, result.getImages().size());
    }
}
