package com.alseinn.semiwood.dao.about;

import com.alseinn.semiwood.entity.about.About;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AboutRepository extends JpaRepository<About, Long> {
}