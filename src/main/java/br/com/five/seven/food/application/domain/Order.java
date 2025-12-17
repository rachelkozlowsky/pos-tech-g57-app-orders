package br.com.five.seven.food.application.domain;

import br.com.five.seven.food.application.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order extends TimeAt{

    private Long id;
    private String title;
    private String description;
    private OrderStatus orderStatus;
    private String cpfClient;
    private List<Item> items = new ArrayList<>();
    private BigDecimal totalAmount;
    private LocalDateTime receivedAt;
    private String remainingTime;

    public Order(Long id, String title, String description, OrderStatus orderStatus, String cpfClient, List<Item> items, BigDecimal totalAmount, LocalDateTime receivedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.orderStatus = orderStatus;
        this.cpfClient = cpfClient;
        this.items = items != null ? items : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.receivedAt = receivedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Order(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCpfClient() {
        return cpfClient;
    }

    public void setCpfClient(String cpfClient) {
        this.cpfClient = cpfClient;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(Item::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }
}
