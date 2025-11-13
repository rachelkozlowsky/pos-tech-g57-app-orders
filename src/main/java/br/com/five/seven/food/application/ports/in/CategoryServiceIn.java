package br.com.five.seven.food.application.ports.in;

import br.com.five.seven.food.application.domain.Category;
import jakarta.xml.bind.ValidationException;

import java.util.List;

public interface CategoryServiceIn {
    Category createCategory(Category domain) throws ValidationException;
    Category getCategoryById(Long categoryId);
    Category getCategoryByName(String categoryName);
    List<Category> getAllCategory();
    Category updateCategory(Long categoryId, Category category) throws ValidationException;
    void deleteCategory(Long categoryId);
}
