package com.alseinn.semiwood.entity.user.concrete;

import com.alseinn.semiwood.entity.item.Item;
import com.alseinn.semiwood.entity.user.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractUser extends Item {
    @Column(nullable = false)
    private String firstname;
    @Column(nullable = false)
    private String lastname;
    @Enumerated(EnumType.STRING)
    private Gender gender;
}