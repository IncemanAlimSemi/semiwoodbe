package com.alseinn.semiwood.entity.order;

import com.alseinn.semiwood.entity.item.Item;
import com.alseinn.semiwood.entity.order.enums.Status;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.event.listeners.ModifiedDateEntityListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@EntityListeners(ModifiedDateEntityListener.class)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer_order")
public class Order extends Item {
    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<OrderItem> orderItems = new HashSet<>();
    @Column(nullable = false)
    private Date date;
    @Column(nullable = false)
    private Status status;
    @Column()
    private String email;
    @Column(nullable = false)
    private Double totalPrice;
}