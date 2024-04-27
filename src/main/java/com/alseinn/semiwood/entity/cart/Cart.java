package com.alseinn.semiwood.entity.cart;

import com.alseinn.semiwood.entity.item.Item;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.event.listeners.ModifiedDateEntityListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@EntityListeners(ModifiedDateEntityListener.class)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer_cart")
public class Cart extends Item {
    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<CartItem> cartItems = new HashSet<>();
    @Column(nullable = false)
    private Double totalPrice;
}
