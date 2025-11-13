package br.com.five.seven.food.application.ports.out;

import br.com.five.seven.food.application.domain.Product;

import java.util.List;

public interface IProductRepositoryOut {
    Product save(Product product);
    Product getById(Long id);
    List<Product> getAll();
    List<Product> getByCategory(String categoryName);
    Product update(Product product);
    void delete(Long id);
}
