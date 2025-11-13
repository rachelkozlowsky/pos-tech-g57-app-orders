package br.com.five.seven.food.application.ports.in;

import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import jakarta.xml.bind.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderServiceIn {
    void deleteById(Long id);
    Order update(Long id, Order order) throws ValidationException;
    Order create(Order order) throws ValidationException;
    Order findById(Long id) throws ValidationException;
    Page<Order> findAll(Pageable pageable);
    Page<Order> findAllByOrderStatus(List<OrderStatus> orderStatus, Pageable pageable);
    Order updateStatusOrder(Long orderId, OrderStatus orderStatus) throws ValidationException;
    Order advanceOrderStatus(Long id) throws ValidationException;
    Order updateOrderCombo(Long id, Order order) throws ValidationException;
}
