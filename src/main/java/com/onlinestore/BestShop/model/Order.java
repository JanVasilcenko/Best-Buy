package com.onlinestore.BestShop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "order")
    private Set<OrderItem> orderItems = new LinkedHashSet<>();
}