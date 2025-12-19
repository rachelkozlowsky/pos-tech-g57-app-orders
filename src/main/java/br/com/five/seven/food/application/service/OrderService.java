package br.com.five.seven.food.application.service;

import br.com.five.seven.food.application.domain.Item;
import br.com.five.seven.food.application.domain.Order;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.domain.enums.OrderStatus;
import br.com.five.seven.food.application.ports.in.CategoryServiceIn;
import br.com.five.seven.food.application.ports.in.OrderServiceIn;
import br.com.five.seven.food.application.ports.out.IClientApiOut;
import br.com.five.seven.food.application.ports.out.IOrderRepositoryOut;
import br.com.five.seven.food.application.ports.out.IProductRepositoryOut;
import br.com.five.seven.food.infra.exceptions.ClientNotFoundException;
import jakarta.xml.bind.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class OrderService implements OrderServiceIn {

    private final IOrderRepositoryOut orderRepository;
    private final IProductRepositoryOut productRepository;
    private final CategoryServiceIn categoryService;
    private final IClientApiOut clientApiOut;

    public OrderService(IOrderRepositoryOut orderRepository, IProductRepositoryOut productRepository,
                        CategoryServiceIn categoryService, IClientApiOut clientApiOut) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.clientApiOut = clientApiOut;
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> findAllByOrderStatus(List<OrderStatus> orderStatus, Pageable pageable) {
        return orderRepository.findAllByOrderStatus(orderStatus, pageable);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id);
    }

    public Order create(Order order) throws ValidationException {
        validateAndPopulateOrder(order);
        return orderRepository.save(order);
    }

    public Order update(Long id, Order order) throws ValidationException {
        Order orderToBeUpdated = findById(id);
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

    public Order updateStatusOrder(Long orderId, OrderStatus orderStatus) {
        Order order = findById(orderId);
        order.setOrderStatus(orderStatus);

        if (orderStatus.equals(OrderStatus.RECEIVED)) {
            order.setReceivedAt(LocalDateTime.now());
        }

        order.setUpdatedAt(LocalDateTime.now());

        return save(order);
    }

    public Order advanceOrderStatus(Long orderId) {
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
    public Order updateOrderItems(Long id, Order order) throws ValidationException {
        Order orderToBeUpdated = findById(id);
        validateAndPopulateOrder(order);
        orderToBeUpdated.setItems(order.getItems());
        orderToBeUpdated.setTotalAmount(orderToBeUpdated.calculateTotalAmount());
        orderToBeUpdated.setUpdatedAt(LocalDateTime.now());
        return orderRepository.update(orderToBeUpdated);
    }

    private void validateAndPopulateOrder(Order order) throws ValidationException {
        // Validate client if CPF is provided
        validateClient(order.getCpfClient());

        List<Item> items = order.getItems();

        if (items == null || items.isEmpty()) {
            throw new ValidationException("Order must have at least one item.");
        }

        validateAndSetProducts(items);

        order.setTotalAmount(order.calculateTotalAmount());
        order.setRemainingTime(calculateTime(order.getReceivedAt(), order.getOrderStatus()));
    }

    private void validateClient(String cpfClient) {
        if (cpfClient != null && !cpfClient.isBlank()) {
            clientApiOut.getClientByCpf(cpfClient)
                .orElseThrow(() -> new ClientNotFoundException(
                    "Client with CPF " + cpfClient + " not found"));
        }
    }

    private void validateAndSetProducts(List<Item> items) throws ValidationException {
        for (Item item : items) {
            if (item.getQuantity() < 1) {
                throw new ValidationException("Each item must have at least quantity 1.");
            }

            Product product = productRepository.getById(item.getProduct().getId());

            if (product == null) {
                throw new ValidationException("Product with ID " + item.getProduct().getId() + " not found.");
            }

            if (!product.isActive()) {
                throw new ValidationException("Product '" + product.getName() + "' is not available.");
            }

            // Validate product category
            if (product.getCategory() == null) {
                throw new ValidationException("Product '" + product.getName() + "' does not have a category assigned.");
            }

            // Ensure category exists and is active
            var category = categoryService.getCategoryById(product.getCategory().getId());
            if (category == null) {
                throw new ValidationException("Category for product '" + product.getName() + "' not found.");
            }

            if (!category.isActive()) {
                throw new ValidationException("Category '" + category.getName() + "' is not active.");
            }

            item.setProduct(product);
        }
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
