package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.ports.out.ICategoryRepositoryOut;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Service BDD Tests")
class CategoryServiceTest {

    @Mock
    private ICategoryRepositoryOut categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    // CREATE CATEGORY TESTS

    @Test
    @DisplayName("Scenario: Successfully create a new category with valid data")
    void givenValidCategory_whenCreatingCategory_thenCategoryShouldBeCreated() throws ValidationException {
        // Given: A valid category with name
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // When: Creating the category
        Category result = categoryService.createCategory(category);

        // Then: The category should be created successfully
        assertNotNull(result, "Created category should not be null");
        assertEquals("Lanches", result.getName(), "Category name should match");
        assertEquals(1L, result.getId(), "Category ID should match");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("Scenario: Fail to create category when name is null")
    void givenCategoryWithNullName_whenCreatingCategory_thenValidationExceptionShouldBeThrown() {
        // Given: A category with null name
        Category category = new Category();
        category.setId(1L);
        category.setName(null);

        // When & Then: Creating the category should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> categoryService.createCategory(category),
            "Should throw ValidationException when name is null"
        );

        assertEquals("Category name cannot be empty", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Scenario: Fail to create category when name is empty")
    void givenCategoryWithEmptyName_whenCreatingCategory_thenValidationExceptionShouldBeThrown() {
        // Given: A category with empty name
        Category category = new Category();
        category.setId(1L);
        category.setName("");

        // When & Then: Creating the category should throw ValidationException
        ValidationException exception = assertThrows(
            ValidationException.class,
            () -> categoryService.createCategory(category),
            "Should throw ValidationException when name is empty"
        );

        assertEquals("Category name cannot be empty", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    // RETRIEVE CATEGORY TESTS

    @Test
    @DisplayName("Scenario: Successfully retrieve category by ID")
    void givenExistingCategoryId_whenGettingCategoryById_thenCategoryShouldBeReturned() {
        // Given: An existing category in the repository
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");
        when(categoryRepository.getById(1L)).thenReturn(category);

        // When: Getting the category by ID
        Category result = categoryService.getCategoryById(1L);

        // Then: The category should be returned
        assertNotNull(result, "Retrieved category should not be null");
        assertEquals(1L, result.getId(), "Category ID should match");
        assertEquals("Lanches", result.getName(), "Category name should match");
        verify(categoryRepository, times(1)).getById(1L);
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve category by name")
    void givenExistingCategoryName_whenGettingCategoryByName_thenCategoryShouldBeReturned() {
        // Given: An existing category with specific name
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");
        when(categoryRepository.getByName("Lanches")).thenReturn(category);

        // When: Getting the category by name
        Category result = categoryService.getCategoryByName("Lanches");

        // Then: The category should be returned
        assertNotNull(result, "Retrieved category should not be null");
        assertEquals("Lanches", result.getName(), "Category name should match");
        verify(categoryRepository, times(1)).getByName("Lanches");
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve all categories")
    void givenMultipleCategories_whenGettingAllCategories_thenAllCategoriesShouldBeReturned() {
        // Given: Multiple categories exist in the repository
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Lanches");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Bebidas");

        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryRepository.getAll()).thenReturn(categories);

        // When: Getting all categories
        List<Category> result = categoryService.getAllCategory();

        // Then: All categories should be returned
        assertNotNull(result, "Retrieved categories should not be null");
        assertEquals(2, result.size(), "Should return all categories");
        verify(categoryRepository, times(1)).getAll();
    }

    // UPDATE CATEGORY TESTS

    @Test
    @DisplayName("Scenario: Successfully update existing category")
    void givenExistingCategory_whenUpdatingCategory_thenCategoryShouldBeUpdated() throws ValidationException {
        // Given: An existing category and updated data
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Lanches");

        Category updatedCategory = new Category();
        updatedCategory.setName("Bebidas");

        when(categoryRepository.getById(1L)).thenReturn(existingCategory);
        when(categoryRepository.update(any(Category.class))).thenReturn(updatedCategory);

        // When: Updating the category
        Category result = categoryService.updateCategory(1L, updatedCategory);

        // Then: The category should be updated
        assertNotNull(result, "Updated category should not be null");
        verify(categoryRepository, times(1)).getById(1L);
        verify(categoryRepository, times(1)).update(updatedCategory);
    }

    // DELETE CATEGORY TESTS

    @Test
    @DisplayName("Scenario: Successfully delete existing category")
    void givenExistingCategory_whenDeletingCategory_thenCategoryShouldBeDeleted() {
        // Given: An existing category
        Category category = new Category();
        category.setId(1L);
        category.setName("Lanches");
        when(categoryRepository.getById(1L)).thenReturn(category);
        doNothing().when(categoryRepository).delete(1L);

        // When: Deleting the category
        categoryService.deleteCategory(1L);

        // Then: The category should be deleted
        verify(categoryRepository, times(1)).getById(1L);
        verify(categoryRepository, times(1)).delete(1L);
    }
}
