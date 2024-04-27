package com.alseinn.semiwood.entity.company;

import com.alseinn.semiwood.entity.item.Item;
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
@Table(name = "company")
public class Company extends Item {
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String taxNumber;
    @Column(nullable = false)
    private Boolean isActive;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(mappedBy = "company", cascade = CascadeType.PERSIST, optional = false)
    private User user;
}