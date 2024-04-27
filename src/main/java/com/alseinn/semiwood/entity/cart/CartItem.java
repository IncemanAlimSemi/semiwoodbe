package com.alseinn.semiwood.entity.cart;

import com.alseinn.semiwood.entity.item.Item;
import com.alseinn.semiwood.entity.product.Product;
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
@Table(name = "cart_item")
public class CartItem extends Item {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Cart cart;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Product product;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private Double totalPrice;
}
