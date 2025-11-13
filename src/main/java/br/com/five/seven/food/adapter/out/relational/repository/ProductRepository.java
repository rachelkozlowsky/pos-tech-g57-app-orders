package br.com.five.seven.food.adapter.out.relational.repository;

import br.com.five.seven.food.adapter.out.relational.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}

