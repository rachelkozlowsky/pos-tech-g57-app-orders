package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.ports.out.ICategoryRepositoryOut;
import jakarta.xml.bind.ValidationException;
import org.junit.jupiter.api.BeforeEach;
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
class CategoryServiceTest {

    @Mock
    private ICategoryRepositoryOut categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Lanches");
    }

    @Test
    void shouldCreateCategorySuccessfully() throws ValidationException {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertNotNull(result);
        assertEquals("Lanches", result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNameIsNull() {
        category.setName(null);

        assertThrows(ValidationException.class, () -> categoryService.createCategory(category));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNameIsEmpty() {
        category.setName("");

        assertThrows(ValidationException.class, () -> categoryService.createCategory(category));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldGetCategoryById() {
        when(categoryRepository.getById(1L)).thenReturn(category);

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Lanches", result.getName());
        verify(categoryRepository, times(1)).getById(1L);
    }

    @Test
    void shouldGetCategoryByName() {
        when(categoryRepository.getByName("Lanches")).thenReturn(category);

        Category result = categoryService.getCategoryByName("Lanches");

        assertNotNull(result);
        assertEquals("Lanches", result.getName());
        verify(categoryRepository, times(1)).getByName("Lanches");
    }

    @Test
    void shouldGetAllCategories() {
        List<Category> categories = Arrays.asList(category, new Category());
        when(categoryRepository.getAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategory();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).getAll();
    }

    @Test
    void shouldUpdateCategorySuccessfully() throws ValidationException {
        Category updatedCategory = new Category();
        updatedCategory.setName("Bebidas");

        when(categoryRepository.getById(1L)).thenReturn(category);
        when(categoryRepository.update(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertNotNull(result);
        verify(categoryRepository, times(1)).getById(1L);
        verify(categoryRepository, times(1)).update(updatedCategory);
    }

    @Test
    void shouldDeleteCategory() {
        when(categoryRepository.getById(1L)).thenReturn(category);
        doNothing().when(categoryRepository).delete(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).getById(1L);
        verify(categoryRepository, times(1)).delete(1L);
    }
}
