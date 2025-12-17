package br.com.five.seven.food.adapter.out.relational.repository;

import br.com.five.seven.food.adapter.out.relational.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByNameIgnoreCase(String name);
}


