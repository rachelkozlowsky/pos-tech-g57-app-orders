package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.ports.in.ProductServiceIn;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;
import jakarta.xml.bind.ValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ProductService implements ProductServiceIn {

    private final IProductRepositoryOut productRepository;

    public ProductService(IProductRepositoryOut productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) throws ValidationException {
        validateProduct(product);
        return productRepository.save(product);
    }


    public Product getProductById(Long id) {
        return productRepository.getById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.getAll();
    }

    @Override
    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.getByCategory(categoryName);
    }

    public Product updateProduct(Long productId, Product product) throws ValidationException {
        validateProduct(product);
        Product existingProduct = productRepository.getById(productId);

        product.setId(existingProduct.getId());

        return productRepository.update(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.getById(id);
        productRepository.delete(product.getId());
    }

    private void validateProduct(Product product) throws ValidationException {
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new ValidationException("Product name cannot be empty");
        }

        if (product.getPrice() == null || Objects.equals(product.getPrice(), BigDecimal.ZERO)) {
            throw new ValidationException("Product price cannot be empty or zero");
        }

        if (product.getCategory() == null) {
            throw new ValidationException("Product category cannot be empty");
        }
    }
}
