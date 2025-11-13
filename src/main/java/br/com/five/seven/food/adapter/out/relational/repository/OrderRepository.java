package br.com.five.seven.food.adapter.out.relational.repository;

import br.com.five.seven.food.adapter.out.relational.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}

