package br.com.five.seven.food.adapter.out.relational.repository;

import br.com.five.seven.food.adapter.out.relational.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}

