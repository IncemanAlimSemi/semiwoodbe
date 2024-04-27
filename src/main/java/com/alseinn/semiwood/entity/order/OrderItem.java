package com.alseinn.semiwood.entity.order;

import com.alseinn.semiwood.entity.item.Item;
import com.alseinn.semiwood.entity.product.Product;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.event.listeners.ModifiedDateEntityListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EntityListeners(ModifiedDateEntityListener.class)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItem extends Item {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Product product;
    @Column(nullable = false)
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column
    private String email;
    @Column(nullable = false)
    private Double totalPrice;
}
