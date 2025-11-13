package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.ports.out.ICategoryRepositoryOut;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    private final ICategoryRepositoryOut ICategoryRepositoryOut = mock(ICategoryRepositoryOut.class);
    private final CategoryService categoryService = new CategoryService(ICategoryRepositoryOut);

    @Test
    public void test_create_category_returns_saved_category() throws ValidationException {

        Category inputCategory = new Category(1L, "Italian", true);
        Category savedCategory = new Category(1L, "Italian", true);

        when(ICategoryRepositoryOut.save(inputCategory)).thenReturn(savedCategory);

        Category result = categoryService.createCategory(inputCategory);

        assertEquals(savedCategory.getId(), result.getId());
        assertEquals(savedCategory.getName(), result.getName());
        assertEquals(savedCategory.isActive(), result.isActive());
        verify(ICategoryRepositoryOut).save(inputCategory);
    }

    @Test
    public void test_get_all_category_returns_categories() {

        Long categoryId = 123L;
        Category expectedCategory = new Category(categoryId, "Mexican", true);

        when(ICategoryRepositoryOut.getAll()).thenReturn(List.of(expectedCategory));

        Category result = categoryService.getAllCategory().getFirst();

        assertEquals(expectedCategory.getId(), result.getId());
        assertEquals(expectedCategory.getName(), result.getName());
        assertEquals(expectedCategory.isActive(), result.isActive());
        verify(ICategoryRepositoryOut).getAll();
    }


    @Test
    public void test_get_category_by_id_returns_correct_category() {

        Long categoryId = 123L;
        Category expectedCategory = new Category(categoryId, "Mexican", true);

        when(ICategoryRepositoryOut.getById(categoryId)).thenReturn(expectedCategory);

        Category result = categoryService.getCategoryById(categoryId);

        assertEquals(expectedCategory.getId(), result.getId());
        assertEquals(expectedCategory.getName(), result.getName());
        assertEquals(expectedCategory.isActive(), result.isActive());
        verify(ICategoryRepositoryOut).getById(categoryId);
    }


    @Test
    public void test_get_category_by_name_returns_correct_category() {

        String categoryName = "Chinese";
        Category expectedCategory = new Category(456L, categoryName, true);

       when(ICategoryRepositoryOut.getByName(categoryName)).thenReturn(expectedCategory);

        Category result = categoryService.getCategoryByName(categoryName);

        assertEquals(expectedCategory.getId(), result.getId());
        assertEquals(expectedCategory.getName(), result.getName());
        assertEquals(expectedCategory.isActive(), result.isActive());
        verify(ICategoryRepositoryOut).getByName(categoryName);
    }

    @Test
    public void test_update_category_with_valid_id_and_data() throws ValidationException {

        Long categoryId = 123L;
        ICategoryRepositoryOut categoryRepository = mock(ICategoryRepositoryOut.class);
        CategoryService categoryService = new CategoryService(categoryRepository);

        Category existingCategory = new Category(categoryId, "Old Name", true);
        Category updatedCategory = new Category(null, "New Name", false);
        Category expectedCategory = new Category(categoryId, "New Name", false);

        when(categoryRepository.getById(categoryId)).thenReturn(existingCategory);
        when(categoryRepository.update(any(Category.class))).thenReturn(expectedCategory);

        Category result = categoryService.updateCategory(categoryId, updatedCategory);

        assertEquals(categoryId, result.getId());
        assertEquals("New Name", result.getName());
        assertFalse(result.isActive());

        verify(categoryRepository).getById(categoryId);
        verify(categoryRepository).update(any(Category.class));
    }

    @Test
    public void test_delete_category_by_id_returns_void() {

        Long categoryId = 123L;
        Category existingCategory = new Category(categoryId, "Mexican", true);

        when(ICategoryRepositoryOut.getById(categoryId)).thenReturn(existingCategory);

        categoryService.deleteCategory(categoryId);

        verify(ICategoryRepositoryOut).getById(categoryId);
        verify(ICategoryRepositoryOut).delete(existingCategory.getId());
    }

    @Test
    public void test_repository_delete_method_called_with_provided_id() {

        Long categoryId = 123L;
        Category existingCategory = new Category(categoryId, "Mexican", true);

        when(ICategoryRepositoryOut.getById(categoryId)).thenReturn(existingCategory);

        categoryService.deleteCategory(categoryId);

        verify(ICategoryRepositoryOut).getById(categoryId);
        verify(ICategoryRepositoryOut).delete(existingCategory.getId());
    }


    @Test
    public void test_create_category_with_null_name_throws_validation_exception() {
        Category nullNameCategory = new Category(null, null, false);

        assertThrows(ValidationException.class, () -> {
            categoryService.createCategory(nullNameCategory);
        });

        verify(ICategoryRepositoryOut, never()).save(nullNameCategory);
    }


    @Test
    public void test_get_category_by_id_that_does_not_exist() {

        Long nonExistentId = 999L;

        when(ICategoryRepositoryOut.getById(nonExistentId)).thenReturn(null);

        Category result = categoryService.getCategoryById(nonExistentId);

        assertNull(result);
        verify(ICategoryRepositoryOut).getById(nonExistentId);
    }


    @Test
    public void test_get_category_by_name_that_does_not_exist() {

        String nonExistentName = "NonExistentCategory";

        when(ICategoryRepositoryOut.getByName(nonExistentName)).thenReturn(null);

        Category result = categoryService.getCategoryByName(nonExistentName);

        assertNull(result);
        verify(ICategoryRepositoryOut).getByName(nonExistentName);
    }
}
