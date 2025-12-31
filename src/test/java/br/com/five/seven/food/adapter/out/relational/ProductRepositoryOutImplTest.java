package br.com.five.seven.food.adapter.out.relational;

import br.com.five.seven.food.adapter.in.mappers.ProductMapper;
import br.com.five.seven.food.adapter.out.relational.entity.CategoryEntity;
import br.com.five.seven.food.adapter.out.relational.entity.ProductEntity;
import br.com.five.seven.food.adapter.out.relational.repository.ProductRepository;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Repository Out Tests")
class ProductRepositoryOutImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductRepositoryOutImpl productRepositoryOut;

    @Test
    @DisplayName("Should save product successfully")
    void givenProduct_whenSaving_thenProductShouldBeSaved() {
        // Given
        Product product = createProduct(null, "Hambúrguer", BigDecimal.valueOf(25.90));
        ProductEntity entity = createProductEntity(1L, "Hambúrguer", BigDecimal.valueOf(25.90));
        Product savedProduct = createProduct(1L, "Hambúrguer", BigDecimal.valueOf(25.90));

        when(productMapper.fromDomain(product)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toDomain(entity)).thenReturn(savedProduct);

        // When
        Product result = productRepositoryOut.save(product);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Hambúrguer", result.getName());
        verify(productRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should get product by ID")
    void givenProductId_whenGettingById_thenProductShouldBeReturned() {
        // Given
        Long productId = 1L;
        ProductEntity entity = createProductEntity(productId, "Hambúrguer", BigDecimal.valueOf(25.90));
        Product product = createProduct(productId, "Hambúrguer", BigDecimal.valueOf(25.90));

        when(productRepository.findById(productId)).thenReturn(Optional.of(entity));
        when(productMapper.toDomain(entity)).thenReturn(product);

        // When
        Product result = productRepositoryOut.getById(productId);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Hambúrguer", result.getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should return null when product not found by ID")
    void givenNonExistentId_whenGettingById_thenNullShouldBeReturned() {
        // Given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When
        Product result = productRepositoryOut.getById(productId);

        // Then
        assertNull(result);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("Should get all products")
    void givenMultipleProducts_whenGettingAll_thenAllProductsShouldBeReturned() {
        // Given
        ProductEntity entity1 = createProductEntity(1L, "Hambúrguer", BigDecimal.valueOf(25.90));
        ProductEntity entity2 = createProductEntity(2L, "Refrigerante", BigDecimal.valueOf(5.00));
        Product product1 = createProduct(1L, "Hambúrguer", BigDecimal.valueOf(25.90));
        Product product2 = createProduct(2L, "Refrigerante", BigDecimal.valueOf(5.00));

        when(productRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));
        when(productMapper.toDomain(entity1)).thenReturn(product1);
        when(productMapper.toDomain(entity2)).thenReturn(product2);

        // When
        List<Product> result = productRepositoryOut.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get products by category")
    void givenCategoryName_whenGettingByCategory_thenProductsInCategoryShouldBeReturned() {
        // Given
        String categoryName = "Lanches";
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName(categoryName);

        ProductEntity entity1 = createProductEntity(1L, "Hambúrguer", BigDecimal.valueOf(25.90));
        entity1.setCategory(categoryEntity);
        
        ProductEntity entity2 = createProductEntity(2L, "Refrigerante", BigDecimal.valueOf(5.00));
        CategoryEntity beverageCategory = new CategoryEntity();
        beverageCategory.setId(2L);
        beverageCategory.setName("Bebidas");
        entity2.setCategory(beverageCategory);

        Product product1 = createProduct(1L, "Hambúrguer", BigDecimal.valueOf(25.90));

        when(productRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));
        when(productMapper.toDomain(entity1)).thenReturn(product1);

        // When
        List<Product> result = productRepositoryOut.getByCategory(categoryName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hambúrguer", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update product")
    void givenProduct_whenUpdating_thenProductShouldBeUpdated() {
        // Given
        Product product = createProduct(1L, "Hambúrguer Updated", BigDecimal.valueOf(30.00));
        ProductEntity entity = createProductEntity(1L, "Hambúrguer Updated", BigDecimal.valueOf(30.00));

        when(productMapper.fromDomain(product)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toDomain(entity)).thenReturn(product);

        // When
        Product result = productRepositoryOut.update(product);

        // Then
        assertNotNull(result);
        assertEquals("Hambúrguer Updated", result.getName());
        verify(productRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should delete product by ID")
    void givenProductId_whenDeleting_thenProductShouldBeDeleted() {
        // Given
        Long productId = 1L;
        doNothing().when(productRepository).deleteById(productId);

        // When
        productRepositoryOut.delete(productId);

        // Then
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("Should handle products with null category when filtering by category")
    void givenProductsWithNullCategory_whenGettingByCategory_thenOnlyProductsWithCategoryShouldBeReturned() {
        // Given
        String categoryName = "Lanches";
        ProductEntity entity1 = createProductEntity(1L, "Hambúrguer", BigDecimal.valueOf(25.90));
        entity1.setCategory(null);

        when(productRepository.findAll()).thenReturn(Arrays.asList(entity1));

        // When
        List<Product> result = productRepositoryOut.getByCategory(categoryName);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(productRepository, times(1)).findAll();
    }

    private Product createProduct(Long id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setActive(true);
        return product;
    }

    private ProductEntity createProductEntity(Long id, String name, BigDecimal price) {
        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setPrice(price);
        entity.setActive(true);
        return entity;
    }
}
