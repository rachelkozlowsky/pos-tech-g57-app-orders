package br.com.five.seven.food.adapter.in.mappers;

import br.com.five.seven.food.adapter.in.payload.category.CategoryRequest;
import br.com.five.seven.food.adapter.out.relational.entity.CategoryEntity;
import br.com.five.seven.food.application.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Category Mapper Tests")
class CategoryMapperTest {

    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        categoryMapper = new CategoryMapperImpl();
    }

    @Test
    @DisplayName("Should map CategoryEntity to Category domain")
    void givenCategoryEntity_whenMappingToDomain_thenCategoryShouldBeCreated() {
        // Given
        CategoryEntity entity = new CategoryEntity();
        entity.setId(1L);
        entity.setName("Lanches");
        entity.setActive(true);

        // When
        Category result = categoryMapper.categoryToDomain(entity);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Lanches", result.getName());
        assertTrue(result.isActive());
    }

    @Test
    @DisplayName("Should map Category domain to CategoryEntity")
    void givenCategoryDomain_whenMappingToEntity_thenCategoryEntityShouldBeCreated() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Bebidas");
        category.setActive(true);

        // When
        CategoryEntity result = categoryMapper.fromDomain(category);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Bebidas", result.getName());
        assertTrue(result.isActive());
    }

    @Test
    @DisplayName("Should map CategoryRequest to Category domain")
    void givenCategoryRequest_whenMappingToDomain_thenCategoryShouldBeCreated() {
        // Given
        CategoryRequest request = new CategoryRequest("Sobremesas", true);

        // When
        Category result = categoryMapper.requestToDomain(request);

        // Then
        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Sobremesas", result.getName());
        assertTrue(result.isActive());
    }

    @Test
    @DisplayName("Should map CategoryRequest to Category domain using requestFromDomain")
    void givenCategoryRequest_whenMappingWithRequestFromDomain_thenCategoryShouldBeCreated() {
        // Given
        CategoryRequest request = new CategoryRequest("Acompanhamentos", false);

        // When
        Category result = categoryMapper.requestFromDomain(request);

        // Then
        assertNotNull(result);
        assertEquals("Acompanhamentos", result.getName());
        assertFalse(result.isActive());
    }

    @Test
    @DisplayName("Should return null when CategoryEntity is null")
    void givenNullCategoryEntity_whenMappingToDomain_thenNullShouldBeReturned() {
        // When
        Category result = categoryMapper.categoryToDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when Category domain is null")
    void givenNullCategory_whenMappingToEntity_thenNullShouldBeReturned() {
        // When
        CategoryEntity result = categoryMapper.fromDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when CategoryRequest is null")
    void givenNullCategoryRequest_whenMappingToDomain_thenNullShouldBeReturned() {
        // When
        Category result = categoryMapper.requestToDomain(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle inactive category mapping")
    void givenInactiveCategory_whenMapping_thenInactiveFlagShouldBeMaintained() {
        // Given
        Category category = new Category();
        category.setId(2L);
        category.setName("Categoria Inativa");
        category.setActive(false);

        // When
        CategoryEntity result = categoryMapper.fromDomain(category);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertFalse(result.isActive());
    }

    @Test
    @DisplayName("Should map CategoryRequest with null active flag")
    void givenCategoryRequestWithNullActive_whenMapping_thenDefaultValueShouldBeUsed() {
        // Given
        CategoryRequest request = new CategoryRequest(null, null);
        request.setName("Categoria Teste");

        // When
        Category result = categoryMapper.requestToDomain(request);

        // Then
        assertNotNull(result);
        assertEquals("Categoria Teste", result.getName());
    }

    @Test
    @DisplayName("Should map CategoryEntity with all fields")
    void givenCompleteCategory_whenMappingBothDirections_thenAllFieldsShouldBeMaintained() {
        // Given
        Category category = new Category();
        category.setId(3L);
        category.setName("Categoria Completa");
        category.setActive(true);

        // When
        CategoryEntity entity = categoryMapper.fromDomain(category);
        Category backToDomain = categoryMapper.categoryToDomain(entity);

        // Then
        assertNotNull(entity);
        assertNotNull(backToDomain);
        assertEquals(category.getId(), backToDomain.getId());
        assertEquals(category.getName(), backToDomain.getName());
        assertEquals(category.isActive(), backToDomain.isActive());
    }
}
