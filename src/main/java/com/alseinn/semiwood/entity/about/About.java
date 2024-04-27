package com.alseinn.semiwood.entity.about;

import com.alseinn.semiwood.entity.item.Item;
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
@Table(name = "about")
public class About extends Item {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 750)
    private String content;
    @ElementCollection
    @Column(nullable = false)
    private List<String> cdnLinks;
}
