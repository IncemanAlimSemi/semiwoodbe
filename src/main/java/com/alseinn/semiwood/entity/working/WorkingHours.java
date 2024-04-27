package com.alseinn.semiwood.entity.working;

import com.alseinn.semiwood.entity.item.Item;
import com.alseinn.semiwood.entity.working.enums.Day;
import com.alseinn.semiwood.event.listeners.ModifiedDateEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EntityListeners(ModifiedDateEntityListener.class)
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "working_hours")
public class WorkingHours extends Item {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Day day;
    @Column(nullable = false)
    private String startHours;
    @Column(nullable = false)
    private String endHours;
}
