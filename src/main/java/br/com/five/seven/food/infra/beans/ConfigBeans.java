package br.com.five.seven.food.infra.beans;

import br.com.five.seven.food.application.ports.out.ICategoryRepositoryOut;
import br.com.five.seven.food.application.ports.out.IClientApiOut;
import br.com.five.seven.food.application.ports.out.IOrderRepositoryOut;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;
import br.com.five.seven.food.application.service.CategoryService;
import br.com.five.seven.food.application.service.OrderService;
import br.com.five.seven.food.application.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ConfigBeans {

    @Bean
    public ProductService productServiceIn(IProductRepositoryOut productRepository) {
        return new ProductService(productRepository);
    }

    @Bean
    public CategoryService categoryServiceIn(ICategoryRepositoryOut categoryRepository) {
        return new CategoryService(categoryRepository);
    }

    @Bean
    public OrderService orderServiceIn(IOrderRepositoryOut orderRepositoryOut, IProductRepositoryOut productRepository, CategoryService categoryService, IClientApiOut clientApi) {
        return new OrderService(orderRepositoryOut, productRepository, categoryService, clientApi);
    }
}
