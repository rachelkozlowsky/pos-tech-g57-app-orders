package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Combo;
import br.com.five.seven.food.application.domain.Item;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import br.com.five.seven.food.application.ports.in.OrderServiceIn;
import br.com.five.seven.food.application.ports.out.IOrderRepositoryOut;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;
import jakarta.xml.bind.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class OrderService implements OrderServiceIn {

    private final IOrderRepositoryOut orderRepository;

    private final IProductRepositoryOut productRepository;

    public OrderService(IOrderRepositoryOut orderRepository, IProductRepositoryOut productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(order -> {
                    try {
                        return validateAndPopulateOrder(order, true);
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public Page<Order> findAllByOrderStatus(List<OrderStatus> orderStatus, Pageable pageable) {
        return orderRepository.findAllByOrderStatus(orderStatus, pageable)
                .map(order -> {
                    try {
                        return validateAndPopulateOrder(order, true);
                    } catch (ValidationException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public Order findById(Long id) throws ValidationException {
        return validateAndPopulateOrder(orderRepository.findById(id), true);
    }

    public Order create(Order order) throws ValidationException {
        validateAndPopulateOrder(order, false);
        var orderSave = orderRepository.save(order);
        validateAndPopulateOrder(orderSave, false);
        return orderSave;
    }

    public Order update(Long id, Order order) throws ValidationException {
        Order orderToBeUpdated = findById(id);
        validateAndPopulateOrder(order, false);
        order.setId(orderToBeUpdated.getId());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.update(order);
    }

    public void deleteById(Long id) {
        orderRepository.delete(id);
    }

    private Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order updateStatusOrder(Long orderId, OrderStatus orderStatus) throws ValidationException {
        Order order = findById(orderId);
        order.setOrderStatus(orderStatus);

        if (orderStatus.equals(OrderStatus.RECEIVED)) {
            order.setReceivedAt(LocalDateTime.now());
        }

        order.setUpdatedAt(LocalDateTime.now());

        return save(order);
    }

    public Order advanceOrderStatus(Long orderId) throws ValidationException {

        Order order = findById(orderId);
        OrderStatus current = order.getOrderStatus();

        if (current == null) {
            throw new IllegalStateException("A ordem não possui status.");
        }
        OrderStatus next = switch (current) {
            case SENT -> OrderStatus.RECEIVED;
            case RECEIVED -> OrderStatus.IN_PREPARATION;
            case IN_PREPARATION -> OrderStatus.READY;
            case READY -> OrderStatus.FINISHED;
            case FINISHED -> throw new IllegalStateException("Não é possível avançar o status deste pedido.");
            default -> throw new IllegalStateException("Status desconhecido.");
        };

        order.setOrderStatus(next);
        if (next == OrderStatus.RECEIVED) {
            order.setReceivedAt(LocalDateTime.now());
        }
        order.setUpdatedAt(LocalDateTime.now());
        return save(order);
    }

    @Override
    public Order updateOrderCombo(Long id, Order order) throws ValidationException {
        Order orderToBeUpdated = findById(id);
        validateAndPopulateOrder(order, false);
        validateAndPopulateOrder(orderToBeUpdated, true);
        orderToBeUpdated.setCombo(order.getCombo());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.update(order);
    }

    private Order validateAndPopulateOrder(Order order, boolean isSearch) throws ValidationException {
        if (order.getCpfClient() != null) {
            //TODO search client cpf validation
        }

        Combo combo = order.getCombo();
        List<Item> snack = combo.getSnack();
        List<Item> garnish = combo.getGarnish();
        List<Item> drink = combo.getDrink();
        List<Item> dessert = combo.getDessert();

        validateAndSetProducts(snack, isSearch);
        validateAndSetProducts(garnish, isSearch);
        validateAndSetProducts(drink, isSearch);
        validateAndSetProducts(dessert, isSearch);

        if (snack.isEmpty() && garnish.isEmpty() && drink.isEmpty() && dessert.isEmpty()) {
            throw new RuntimeException("Order cannot be empty.");
        }

        order.setTotalAmount(combo.getTotalPrice());
        order.setRemainingTime(calculateTime(order.getReceivedAt(), order.getOrderStatus()));

        return order;
    }

    private void validateAndSetProducts(List<Item> items, boolean isSearch) {
        items.forEach(item -> {
            if (!isSearch && item.getQuantity() < 1) {
                throw new RuntimeException("The combo must have at least one product with quantity 1.");
            }

            Product product = productRepository.getById(item.getProduct().getId());

            if (!isSearch && !product.isActive()) {
                throw new RuntimeException("One or more products in the combo are not available.");
            }

            item.setProduct(product);
        });
    }

    public static String calculateTime(LocalDateTime initial, OrderStatus orderStatus) {

        if (orderStatus.equals(OrderStatus.FINISHED)) {
            return "Pedido entregue ao cliente";
        }
        if (orderStatus.equals(OrderStatus.READY)) {
            return "Pedindo pronto para retirada";
        }
        if (initial == null) {
            return null;
        }

        LocalDateTime dateFinal = initial.plusMinutes(30);
        LocalDateTime now = LocalDateTime.now();
        Duration durationRemainder = Duration.between(now, dateFinal);

        if (durationRemainder.isNegative()) {
            return "O prazo de preparacao do pedido expirou";
        }
        long minutesRemainder = durationRemainder.toMinutes();
        long secondsRemainder = durationRemainder.getSeconds() % 60;

        return String.format("Tempo restante: %d minutos e %d segundos",
                minutesRemainder, secondsRemainder);
    }
}
