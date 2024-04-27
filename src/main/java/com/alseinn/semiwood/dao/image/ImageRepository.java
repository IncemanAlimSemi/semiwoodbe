package com.alseinn.semiwood.dao.image;

import com.alseinn.semiwood.entity.image.Image;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface ImageRepository extends JpaRepository<Image, Long> {
}
