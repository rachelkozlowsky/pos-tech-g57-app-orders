package br.com.five.seven.food.application.ports.in;

import br.com.five.seven.food.application.domain.Product;
import jakarta.xml.bind.ValidationException;

import java.util.List;

public interface ProductServiceIn {
    Product createProduct(Product domain) throws ValidationException;
    Product getProductById(Long productId);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String categoryName);
    Product updateProduct(Long productId, Product product) throws ValidationException;
    void deleteProduct(Long productId);
}
