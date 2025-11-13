package br.com.five.seven.food.adapter.in.mappers;

import br.com.five.seven.food.adapter.in.payload.category.CategoryRequest;
import br.com.five.seven.food.adapter.out.relational.entity.CategoryEntity;
import br.com.five.seven.food.application.domain.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category categoryToDomain(CategoryEntity category);
    CategoryEntity fromDomain(Category category);
    Category requestFromDomain(CategoryRequest category);
    Category requestToDomain(CategoryRequest categoryRequest);
}
