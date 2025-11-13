package br.com.five.seven.food.application.ports.out;

import br.com.five.seven.food.application.domain.Category;

import java.util.List;

public interface ICategoryRepositoryOut {
    Category save(Category category);
    Category getById(Long categoryId);
    Category getByName(String categoryName);
    List<Category> getAll();
    Category update(Category category);
    void delete(Long categoryid);
}
