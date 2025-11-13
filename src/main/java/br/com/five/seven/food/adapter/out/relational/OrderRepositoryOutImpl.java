package br.com.five.seven.food.adapter.out.relational;

import br.com.five.seven.food.adapter.in.mappers.OrderMapper;
import br.com.five.seven.food.adapter.out.relational.entity.OrderEntity;
import br.com.five.seven.food.adapter.out.relational.repository.OrderRepository;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import br.com.five.seven.food.application.ports.out.IOrderRepositoryOut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryOutImpl implements IOrderRepositoryOut {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderRepositoryOutImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    private OrderEntity toEntity(Order order) {
        return orderMapper.domainToEntity(order);
    }

    private Order toDomain(OrderEntity entity) {
        // TODO: Map all fields and relationships as needed
        Order order = new Order();
        order.setId(entity.getId());
        order.setTitle(entity.getTitle());
        order.setDescription(entity.getDescription());
        order.setOrderStatus(OrderStatus.valueOf(entity.getOrderStatus()));
        order.setCpfClient(entity.getCpfClient());
        order.setTotalAmount(entity.getTotalAmount());
        order.setReceivedAt(entity.getReceivedAt());
        order.setRemainingTime(entity.getRemainingTime());
        order.setCreatedAt(entity.getCreatedAt());
        order.setUpdatedAt(entity.getUpdatedAt());
        return order;
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public Page<Order> findAllByOrderStatus(List<OrderStatus> orderStatus, Pageable pageable) {
        List<String> statusNames = orderStatus.stream().map(Enum::name).toList();
        return orderRepository.findAll(pageable)
                .map(this::toDomain)
                .map(order -> statusNames.contains(order.getOrderStatus().name()) ? order : null);
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).map(this::toDomain).orElse(null);
    }

    @Override
    public Order save(Order order) {
        return toDomain(orderRepository.save(toEntity(order)));
    }

    @Override
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order update(Order order) {
        return save(order);
    }
}

