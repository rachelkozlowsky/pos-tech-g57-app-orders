package br.com.five.seven.food.adapter.in.controller;

import br.com.five.seven.food.adapter.in.mappers.CategoryMapper;
import br.com.five.seven.food.adapter.in.payload.category.CategoryRequest;
import br.com.five.seven.food.adapter.in.payload.category.CategoryResponse;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.ports.in.CategoryServiceIn;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class CategoryControllerTest {

    private final CategoryServiceIn categoryService = mock(CategoryServiceIn.class);
    private final CategoryMapper mapper = mock(CategoryMapper.class);
    private final CategoryController controller = new CategoryController(categoryService, mapper);

    @Test
    public void test_create_category_returns_200_ok_with_correct_response() throws ValidationException {

        CategoryRequest request = new CategoryRequest("Test Category", true);
        Category createdCategory = new Category(123L, "Test Category", true);

        when(categoryService.createCategory(any(Category.class))).thenReturn(createdCategory);
        when(mapper.requestFromDomain(request))
                .thenReturn(createdCategory);
        ResponseEntity<CategoryResponse> response = controller.createCategory(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(123L, response.getBody().getId());
        assertEquals("Test Category", response.getBody().getName());
        assertEquals(true, response.getBody().getActive());
    }

    @Test
    public void test_get_category_by_id_returns_200_ok_with_correct_response() {

        Long categoryId = 123L;
        Category category = new Category(categoryId, "Test Category", true);

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        ResponseEntity<CategoryResponse> response = controller.getCategoryById(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(categoryId, response.getBody().getId());
        assertEquals("Test Category", response.getBody().getName());
        assertEquals(true, response.getBody().getActive());
    }

    @Test
    public void test_get_category_by_name_returns_200_ok_with_correct_response() {

        String categoryName = "Test Category";
        Category category = new Category(123L, categoryName, true);

        when(categoryService.getCategoryByName(categoryName)).thenReturn(category);

        ResponseEntity<CategoryResponse> response = controller.getCategoryByName(categoryName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(123L, response.getBody().getId());
        assertEquals(categoryName, response.getBody().getName());
        assertEquals(true, response.getBody().getActive());
    }

    @Test
    public void test_get_non_existent_category_by_id_returns_404_not_found() {

        Long nonExistentCategoryId = 999L;

        when(categoryService.getCategoryById(nonExistentCategoryId)).thenReturn(null);

        ResponseEntity<CategoryResponse> response = controller.getCategoryById(nonExistentCategoryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    public void test_get_non_existent_category_by_name_returns_404_not_found() {

        String nonExistentCategoryName = "Non-existent Category";

        when(categoryService.getCategoryByName(nonExistentCategoryName)).thenReturn(null);

        ResponseEntity<CategoryResponse> response = controller.getCategoryByName(nonExistentCategoryName);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void test_update_category_returns_200_ok_with_correct_response() throws ValidationException {

        Long categoryId = 123L;
        CategoryRequest updateRequest = new CategoryRequest("Updated Category", true);
        Category updatedCategory = new Category(categoryId, "Updated Category", true);

        when(categoryService.getCategoryById(categoryId)).thenReturn(updatedCategory);
        when(categoryService.updateCategory(any(), any())).thenReturn(updatedCategory);

        ResponseEntity<CategoryResponse> response = controller.updateCategory(categoryId, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(categoryId, response.getBody().getId());
        assertEquals("Updated Category", response.getBody().getName());
        assertEquals(true, response.getBody().getActive());
    }

    @Test
    public void test_update_category_not_found() throws ValidationException {


        Long nonExistentCategoryId = 999L;
        CategoryRequest request = new CategoryRequest("Updated Category", true);
        Category mappedCategory = new Category();
        mappedCategory.setName("Updated Category");
        mappedCategory.setActive(true);

        when(mapper.requestToDomain(request)).thenReturn(mappedCategory);
        when(categoryService.updateCategory(eq(nonExistentCategoryId), any(Category.class)))
                .thenThrow(new NoSuchElementException("Category not found with id: " + nonExistentCategoryId));
        assertThrows(NoSuchElementException.class, () -> {
            controller.updateCategory(nonExistentCategoryId, request);
        });

        verify(mapper).requestToDomain(request);
        verify(categoryService).updateCategory(eq(nonExistentCategoryId), any(Category.class));
    }

    @Test
    public void test_getAllCategory_returns_200_ok_with_correct_response() {

        Category category1 = new Category(123L, "Category 1", true);
        Category category2 = new Category(456L, "Category 2", true);

        when(categoryService.getAllCategory()).thenReturn(List.of(category1, category2));

        ResponseEntity<List<CategoryResponse>> response = controller.getAllCategory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(123L, response.getBody().get(0).getId());
        assertEquals(456L, response.getBody().get(1).getId());
    }

    @Test
    public void test_delete_category_returns_204_no_content() {

        Long categoryId = 123L;

        ResponseEntity<Void> response = controller.deleteCategory(categoryId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
