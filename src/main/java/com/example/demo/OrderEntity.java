package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Setter
@Getter
@Entity
public class OrderEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(nullable = false, unique = true)
    private Long id;

    private boolean issued = false;

//    @OneToMany(cascade = CascadeType.ALL,
//               orphanRemoval = true,
//               mappedBy = "order",
//               fetch = FetchType.EAGER)
//    private Set<OrderItemEntity> orderItems;

    // Замена говорит о том, что много элементов заказа могут принадлежать одному заказу.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;
}
