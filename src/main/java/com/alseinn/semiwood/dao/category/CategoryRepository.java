package com.alseinn.semiwood.dao.category;

import com.alseinn.semiwood.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByIsActive(Boolean isActive);
    Optional<Category> findByName(String name);
    Optional<Category> findByIdAndIsActive(Long id, Boolean isActive);
}
