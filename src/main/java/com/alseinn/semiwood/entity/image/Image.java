package com.alseinn.semiwood.entity.image;

import com.alseinn.semiwood.entity.item.Item;
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
@Table(name = "image")
public class Image extends Item {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String type;
    @Lob
    @Column(name = "image_data", length = 100000)
    private byte[] imageData;
}
