package br.com.five.seven.food.adapter.in.controller;

import br.com.five.seven.food.adapter.in.mappers.CategoryMapper;
import br.com.five.seven.food.adapter.in.payload.category.CategoryRequest;
import br.com.five.seven.food.adapter.in.payload.category.CategoryResponse;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.ports.in.CategoryServiceIn;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Controller BDD Tests")
class CategoryControllerTest {

    @Mock
    private CategoryServiceIn categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryController categoryController;

    // CREATE CATEGORY TESTS

    @Test
    @DisplayName("Scenario: Successfully create category")
    void givenValidCategoryRequest_whenCreatingCategory_thenCategoryShouldBeCreatedAndReturned() throws ValidationException {
        // Given: A valid category request
        CategoryRequest request = new CategoryRequest("Lanches", true);
        Category category = createCategory(1L, "Lanches", true);
        CategoryResponse response = createCategoryResponse(1L, "Lanches", true);

        when(categoryMapper.requestFromDomain(any(CategoryRequest.class))).thenReturn(category);
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        // When: Creating category
        ResponseEntity<CategoryResponse> result = categoryController.createCategory(request);

        // Then: Category should be created
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        assertEquals("Lanches", result.getBody().getName());
        assertTrue(result.getBody().getActive());
        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    @DisplayName("Scenario: Fail to create category with invalid data")
    void givenInvalidCategoryRequest_whenCreatingCategory_thenValidationExceptionShouldBeThrown() throws ValidationException {
        // Given: An invalid category request
        CategoryRequest request = new CategoryRequest("", true);
        Category category = new Category();
        category.setName("");

        when(categoryMapper.requestFromDomain(any(CategoryRequest.class))).thenReturn(category);
        when(categoryService.createCategory(any(Category.class)))
                .thenThrow(new ValidationException("Category name cannot be empty"));

        // When & Then: Validation exception should be thrown
        assertThrows(ValidationException.class, () -> categoryController.createCategory(request));
    }

    @Test
    @DisplayName("Scenario: Successfully create inactive category")
    void givenInactiveCategoryRequest_whenCreatingCategory_thenInactiveCategoryShouldBeCreated() throws ValidationException {
        // Given: An inactive category request
        CategoryRequest request = new CategoryRequest("Sobremesas", false);
        Category category = createCategory(3L, "Sobremesas", false);

        when(categoryMapper.requestFromDomain(any(CategoryRequest.class))).thenReturn(category);
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        // When: Creating category
        ResponseEntity<CategoryResponse> result = categoryController.createCategory(request);

        // Then:  Category should be created
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().getActive());
    }

    @Test
    @DisplayName("Scenario: Fail to create duplicate category")
    void givenDuplicateCategoryName_whenCreatingCategory_thenExceptionShouldBeThrown() throws ValidationException {
        // Given: A category with duplicate name
        CategoryRequest request = new CategoryRequest("Lanches", true);
        Category category = new Category();
        category.setName("Lanches");

        when(categoryMapper.requestFromDomain(any(CategoryRequest.class))).thenReturn(category);
        when(categoryService.createCategory(any(Category.class)))
                .thenThrow(new IllegalStateException("Category already exists"));

        // When & Then: Exception should be thrown
        assertThrows(IllegalStateException.class, () -> categoryController.createCategory(request));
    }

    // RETRIEVE CATEGORY TESTS

    @Test
    @DisplayName("Scenario: Successfully retrieve category by ID")
    void givenExistingCategoryId_whenGettingCategoryById_thenCategoryShouldBeReturned() {
        // Given: An existing category
        Category category = createCategory(1L, "Lanches", true);

        when(categoryService.getCategoryById(1L)).thenReturn(category);

        // When: Getting category by ID
        ResponseEntity<CategoryResponse> response = categoryController.getCategoryById(1L);

        // Then: Category should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Lanches", response.getBody().getName());
        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    @DisplayName("Scenario: Return 404 when category not found by ID")
    void givenNonExistingCategoryId_whenGettingCategoryById_thenNotFoundShouldBeReturned() {
        // Given: A non-existing category ID
        when(categoryService.getCategoryById(999L)).thenReturn(null);

        // When: Getting category by ID
        ResponseEntity<CategoryResponse> response = categoryController.getCategoryById(999L);

        // Then: 404 should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(categoryService, times(1)).getCategoryById(999L);
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve category by name")
    void givenExistingCategoryName_whenGettingCategoryByName_thenCategoryShouldBeReturned() {
        // Given: An existing category
        Category category = createCategory(1L, "Lanches", true);

        when(categoryService.getCategoryByName("Lanches")).thenReturn(category);

        // When: Getting category by name
        ResponseEntity<CategoryResponse> response = categoryController.getCategoryByName("Lanches");

        // Then: Category should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Lanches", response.getBody().getName());
        verify(categoryService, times(1)).getCategoryByName("Lanches");
    }

    @Test
    @DisplayName("Scenario: Return 404 when category not found by name")
    void givenNonExistingCategoryName_whenGettingCategoryByName_thenNotFoundShouldBeReturned() {
        // Given: A non-existing category name
        when(categoryService.getCategoryByName("NonExisting")).thenReturn(null);

        // When: Getting category by name
        ResponseEntity<CategoryResponse> response = categoryController.getCategoryByName("NonExisting");

        // Then: 404 should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(categoryService, times(1)).getCategoryByName("NonExisting");
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve all categories")
    void givenMultipleCategories_whenGettingAllCategories_thenAllCategoriesShouldBeReturned() {
        // Given: Multiple categories exist
        Category category1 = createCategory(1L, "Lanches", true);
        Category category2 = createCategory(2L, "Bebidas", true);

        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryService.getAllCategory()).thenReturn(categories);

        // When: Getting all categories
        ResponseEntity<List<CategoryResponse>> response = categoryController.getAllCategory();

        // Then: All categories should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(categoryService, times(1)).getAllCategory();
    }

    @Test
    @DisplayName("Scenario: Return empty list when no categories exist")
    void givenNoCategories_whenGettingAllCategories_thenEmptyListShouldBeReturned() {
        // Given: No categories exist
        when(categoryService.getAllCategory()).thenReturn(List.of());

        // When: Getting all categories
        ResponseEntity<List<CategoryResponse>> response = categoryController.getAllCategory();

        // Then: Empty list should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("Scenario: Successfully retrieve active and inactive categories")
    void givenActiveAndInactiveCategories_whenGettingAllCategories_thenAllCategoriesShouldBeReturned() {
        // Given: Mix of active and inactive categories
        Category category1 = createCategory(1L, "Lanches", true);
        Category category2 = createCategory(2L, "Bebidas", false);

        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryService.getAllCategory()).thenReturn(categories);

        // When: Getting all categories
        ResponseEntity<List<CategoryResponse>> response = categoryController.getAllCategory();

        // Then: All categories should be returned
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    // UPDATE CATEGORY TESTS

    @Test
    @DisplayName("Scenario: Successfully update category")
    void givenExistingCategory_whenUpdatingCategory_thenCategoryShouldBeUpdated() throws ValidationException {
        // Given: An existing category and valid update request
        CategoryRequest request = new CategoryRequest("Bebidas", true);
        Category updatedCategory = createCategory(1L, "Bebidas", true);
        CategoryResponse response = createCategoryResponse(1L, "Bebidas", true);

        when(categoryMapper.requestToDomain(any(CategoryRequest.class))).thenReturn(updatedCategory);
        when(categoryService.updateCategory(anyLong(), any(Category.class))).thenReturn(updatedCategory);

        // When: Updating category
        ResponseEntity<CategoryResponse> result = categoryController.updateCategory(1L, request);

        // Then: Category should be updated
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Bebidas", result.getBody().getName());
        verify(categoryService, times(1)).updateCategory(anyLong(), any(Category.class));
    }

    @Test
    @DisplayName("Scenario: Successfully toggle category active status")
    void givenExistingCategory_whenUpdatingActiveStatus_thenStatusShouldBeToggled() throws ValidationException {
        // Given: An existing active category to be made inactive
        CategoryRequest request = new CategoryRequest("Lanches", false);
        Category updatedCategory = createCategory(1L, "Lanches", false);

        when(categoryMapper.requestToDomain(any(CategoryRequest.class))).thenReturn(updatedCategory);
        when(categoryService.updateCategory(anyLong(), any(Category.class))).thenReturn(updatedCategory);

        // When: Updating category
        ResponseEntity<CategoryResponse> result = categoryController.updateCategory(1L, request);

        // Then: Status should be toggled
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertFalse(result.getBody().getActive());
    }

    @Test
    @DisplayName("Scenario: Fail to update non-existing category")
    void givenNonExistingCategoryId_whenUpdatingCategory_thenExceptionShouldBeThrown() throws ValidationException {
        // Given: A non-existing category ID
        CategoryRequest request = new CategoryRequest("Bebidas", true);
        Category category = new Category();

        when(categoryMapper.requestToDomain(any(CategoryRequest.class))).thenReturn(category);
        when(categoryService.updateCategory(eq(999L), any(Category.class)))
                .thenThrow(new RuntimeException("Category not found"));

        // When & Then: Exception should be thrown
        assertThrows(RuntimeException.class, () -> categoryController.updateCategory(999L, request));
    }

    // DELETE CATEGORY TESTS

    @Test
    @DisplayName("Scenario: Successfully delete category")
    void givenExistingCategory_whenDeletingCategory_thenCategoryShouldBeDeleted() {
        // Given: An existing category
        doNothing().when(categoryService).deleteCategory(1L);

        // When: Deleting category
        ResponseEntity<Void> response = categoryController.deleteCategory(1L);

        // Then: Category should be deleted
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    @DisplayName("Scenario: Fail to delete non-existing category")
    void givenNonExistingCategory_whenDeletingCategory_thenExceptionShouldBeThrown() {
        // Given: A non-existing category
        doThrow(new RuntimeException("Category not found")).when(categoryService).deleteCategory(999L);

        // When & Then: Exception should be thrown
        assertThrows(RuntimeException.class, () -> categoryController.deleteCategory(999L));
    }

    // Helper methods
    private Category createCategory(Long id, String name, boolean active) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setActive(active);
        return category;
    }

    private CategoryResponse createCategoryResponse(Long id, String name, boolean active) {
        CategoryResponse response = new CategoryResponse();
        response.setId(id);
        response.setName(name);
        response.setActive(active);
        return response;
    }
}

