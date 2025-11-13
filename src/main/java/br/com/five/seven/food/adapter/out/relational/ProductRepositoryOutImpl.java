package br.com.five.seven.food.adapter.out.relational;

import br.com.five.seven.food.adapter.in.mappers.ProductMapper;
import br.com.five.seven.food.adapter.out.relational.entity.ProductEntity;
import br.com.five.seven.food.adapter.out.relational.repository.ProductRepository;
import br.com.five.seven.food.adapter.out.relational.repository.CategoryRepository;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductRepositoryOutImpl implements IProductRepositoryOut {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductRepositoryOutImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    private ProductEntity toEntity(Product product) {
        return productMapper.fromDomain(product);
    }

    private Product toDomain(ProductEntity entity) {
        return productMapper.toDomain(entity);
    }

    @Override
    public Product save(Product product) {
        return toDomain(productRepository.save(toEntity(product)));
    }

    @Override
    public Product getById(Long id) {
        return productRepository.findById(id).map(this::toDomain).orElse(null);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Product> getByCategory(String categoryName) {
        return productRepository.findAll().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getName().equalsIgnoreCase(categoryName))
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Product update(Product product) {
        return save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}

