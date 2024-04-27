package com.alseinn.semiwood.entity.contact;

import com.alseinn.semiwood.entity.item.Item;
import com.alseinn.semiwood.entity.working.WorkingHours;
import com.alseinn.semiwood.event.listeners.ModifiedDateEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EntityListeners(ModifiedDateEntityListener.class)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "company_contact")
public class CompanyContact extends Item {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String instagram;
    @Column(nullable = false)
    private String linkedin;
    @Column(nullable = false)
    private String twitter;
    @Column(nullable = false)
    private String facebook;
    @OneToMany(fetch = FetchType.LAZY)
    private List<WorkingHours> workingHours;
}
