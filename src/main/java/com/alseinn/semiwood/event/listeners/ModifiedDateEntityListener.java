package com.alseinn.semiwood.event.listeners;

import com.alseinn.semiwood.entity.item.Item;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.util.Date;

public class ModifiedDateEntityListener {
    @PrePersist
    @PreUpdate
    public void prePersistOrUpdate(Item item) {
        item.setTimeModified(new Date(System.currentTimeMillis()));
    }
}
