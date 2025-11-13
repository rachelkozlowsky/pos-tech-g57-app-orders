package br.com.five.seven.food.application.ports.out;

import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderRepositoryOut {

    Page<Order> findAll(Pageable pageable);

    Page<Order> findAllByOrderStatus(List<OrderStatus> orderStatus, Pageable pageable);

    Order findById(Long id);;

    Order save(Order order);

    void delete(Long id);

    Order update(Order order);

}
