package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.ports.in.CategoryServiceIn;
import br.com.five.seven.food.application.ports.out.ICategoryRepositoryOut;
import jakarta.xml.bind.ValidationException;

import java.util.List;

public class CategoryService implements CategoryServiceIn {

    private final ICategoryRepositoryOut categoryRepository;

    public CategoryService(ICategoryRepositoryOut categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category) throws ValidationException {
        validationCategory(category);
        return categoryRepository.save(category);
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.getById(categoryId);
    }

    public Category getCategoryByName(String categoryName) {
        return categoryRepository.getByName(categoryName);
    }

    public List<Category> getAllCategory() {
        return categoryRepository.getAll();
    }

    public Category updateCategory(Long categoryId, Category category) throws ValidationException {
        validationCategory(category);
        Category existingCategory = categoryRepository.getById(categoryId);
        category.setId(existingCategory.getId());
        return categoryRepository.update(category);
    }

    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.getById(categoryId);
        categoryRepository.delete(category.getId());
    }

    private void validationCategory(Category category) throws ValidationException {
        if (category.getName() == null || category.getName().isEmpty()) {
            throw new ValidationException("Category name cannot be empty");
        }
    }
}
