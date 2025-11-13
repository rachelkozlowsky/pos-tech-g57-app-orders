package br.com.five.seven.food.adapter.in.mappers;

import br.com.five.seven.food.adapter.in.payload.products.ProductRequest;
import br.com.five.seven.food.adapter.out.relational.entity.ProductEntity;
import br.com.five.seven.food.application.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toDomain(ProductEntity productEntity);
    ProductEntity fromDomain(Product product);
    @Mapping(target = "category", ignore = true)
    Product requestToDomain(ProductRequest productRequest);
}
