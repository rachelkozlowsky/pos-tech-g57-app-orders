package br.com.five.seven.food.adapter.out.relational.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String orderStatus;

    @Column(nullable = false)
    private String cpfClient;

    @Transient
    private ComboEntity combo;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    @Column
    private String remainingTime;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public OrderEntity() {
    }

    public OrderEntity(Long id, String title, String description, String orderStatus, String cpfClient, ComboEntity combo, BigDecimal totalAmount, LocalDateTime receivedAt, String remainingTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.orderStatus = orderStatus;
        this.cpfClient = cpfClient;
        this.combo = combo;
        this.totalAmount = totalAmount;
        this.receivedAt = receivedAt;
        this.remainingTime = remainingTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public String getCpfClient() { return cpfClient; }
    public void setCpfClient(String cpfClient) { this.cpfClient = cpfClient; }
    public ComboEntity getCombo() { return combo; }
    public void setCombo(ComboEntity combo) { this.combo = combo; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }
    public String getRemainingTime() { return remainingTime; }
    public void setRemainingTime(String remainingTime) { this.remainingTime = remainingTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

