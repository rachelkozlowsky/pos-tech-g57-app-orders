package br.com.five.seven.food.adapter.out.relational;

import br.com.five.seven.food.adapter.in.mappers.CategoryMapper;
import br.com.five.seven.food.adapter.out.relational.entity.CategoryEntity;
import br.com.five.seven.food.adapter.out.relational.repository.CategoryRepository;
import br.com.five.seven.food.application.domain.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Repository Out Tests")
class CategoryRepositoryOutImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryRepositoryOutImpl categoryRepositoryOut;

    @Test
    @DisplayName("Should save category successfully")
    void givenCategory_whenSaving_thenCategoryShouldBeSaved() {
        // Given
        Category category = createCategory(null, "Lanches", true);
        CategoryEntity entity = createCategoryEntity(1L, "Lanches", true);
        Category savedCategory = createCategory(1L, "Lanches", true);

        when(categoryMapper.fromDomain(category)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(entity);
        when(categoryMapper.categoryToDomain(entity)).thenReturn(savedCategory);

        // When
        Category result = categoryRepositoryOut.save(category);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Lanches", result.getName());
        verify(categoryRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should get category by ID")
    void givenCategoryId_whenGettingById_thenCategoryShouldBeReturned() {
        // Given
        Long categoryId = 1L;
        CategoryEntity entity = createCategoryEntity(categoryId, "Lanches", true);
        Category category = createCategory(categoryId, "Lanches", true);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(entity));
        when(categoryMapper.categoryToDomain(entity)).thenReturn(category);

        // When
        Category result = categoryRepositoryOut.getById(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Lanches", result.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Should return null when category not found by ID")
    void givenNonExistentId_whenGettingById_thenNullShouldBeReturned() {
        // Given
        Long categoryId = 999L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When
        Category result = categoryRepositoryOut.getById(categoryId);

        // Then
        assertNull(result);
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Should get category by name")
    void givenCategoryName_whenGettingByName_thenCategoryShouldBeReturned() {
        // Given
        String categoryName = "Lanches";
        CategoryEntity entity = createCategoryEntity(1L, categoryName, true);
        Category category = createCategory(1L, categoryName, true);

        when(categoryRepository.findByNameIgnoreCase(categoryName)).thenReturn(Optional.of(entity));
        when(categoryMapper.categoryToDomain(entity)).thenReturn(category);

        // When
        Category result = categoryRepositoryOut.getByName(categoryName);

        // Then
        assertNotNull(result);
        assertEquals(categoryName, result.getName());
        verify(categoryRepository, times(1)).findByNameIgnoreCase(categoryName);
    }

    @Test
    @DisplayName("Should return null when category not found by name")
    void givenNonExistentName_whenGettingByName_thenNullShouldBeReturned() {
        // Given
        String categoryName = "NonExistent";
        when(categoryRepository.findByNameIgnoreCase(categoryName)).thenReturn(Optional.empty());

        // When
        Category result = categoryRepositoryOut.getByName(categoryName);

        // Then
        assertNull(result);
        verify(categoryRepository, times(1)).findByNameIgnoreCase(categoryName);
    }

    @Test
    @DisplayName("Should get all categories")
    void givenMultipleCategories_whenGettingAll_thenAllCategoriesShouldBeReturned() {
        // Given
        CategoryEntity entity1 = createCategoryEntity(1L, "Lanches", true);
        CategoryEntity entity2 = createCategoryEntity(2L, "Bebidas", true);
        Category category1 = createCategory(1L, "Lanches", true);
        Category category2 = createCategory(2L, "Bebidas", true);

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));
        when(categoryMapper.categoryToDomain(entity1)).thenReturn(category1);
        when(categoryMapper.categoryToDomain(entity2)).thenReturn(category2);

        // When
        List<Category> result = categoryRepositoryOut.getAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update category")
    void givenCategory_whenUpdating_thenCategoryShouldBeUpdated() {
        // Given
        Category category = createCategory(1L, "Lanches Updated", true);
        CategoryEntity entity = createCategoryEntity(1L, "Lanches Updated", true);

        when(categoryMapper.fromDomain(category)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(entity);
        when(categoryMapper.categoryToDomain(entity)).thenReturn(category);

        // When
        Category result = categoryRepositoryOut.update(category);

        // Then
        assertNotNull(result);
        assertEquals("Lanches Updated", result.getName());
        verify(categoryRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Should delete category by ID")
    void givenCategoryId_whenDeleting_thenCategoryShouldBeDeleted() {
        // Given
        Long categoryId = 1L;
        doNothing().when(categoryRepository).deleteById(categoryId);

        // When
        categoryRepositoryOut.delete(categoryId);

        // Then
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    private Category createCategory(Long id, String name, boolean active) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setActive(active);
        return category;
    }

    private CategoryEntity createCategoryEntity(Long id, String name, boolean active) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setActive(active);
        return entity;
    }
}
