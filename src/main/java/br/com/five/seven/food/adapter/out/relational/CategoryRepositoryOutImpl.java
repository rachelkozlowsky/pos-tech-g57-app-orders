package br.com.five.seven.food.adapter.out.relational;

import br.com.five.seven.food.adapter.in.mappers.CategoryMapper;
import br.com.five.seven.food.adapter.out.relational.entity.CategoryEntity;
import br.com.five.seven.food.adapter.out.relational.repository.CategoryRepository;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.ports.out.ICategoryRepositoryOut;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryRepositoryOutImpl implements ICategoryRepositoryOut {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryRepositoryOutImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    private CategoryEntity toEntity(Category category) {
        return categoryMapper.fromDomain(category);
    }

    private Category toDomain(CategoryEntity entity) {
        return categoryMapper.categoryToDomain(entity);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = toEntity(category);
        return toDomain(categoryRepository.save(entity));
    }

    @Override
    public Category getById(Long categoryId) {
        return categoryRepository.findById(categoryId).map(this::toDomain).orElse(null);
    }

    @Override
    public Category getByName(String categoryName) {
        return categoryRepository.findByNameIgnoreCase(categoryName)
                .map(this::toDomain)
                .orElse(null);
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Category update(Category category) {
        return save(category);
    }

    @Override
    public void delete(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}

