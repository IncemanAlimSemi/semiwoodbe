package com.alseinn.semiwood.dao.product;

import com.alseinn.semiwood.entity.category.Category;
import com.alseinn.semiwood.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByCategoriesInAndIsActive(Set<Category> categories, Boolean isActive, Pageable pageable);
    @Query(value = "SELECT e FROM Product e WHERE (LOWER(e.name) LIKE %:name% OR CAST(e.id AS string) LIKE %:id%) AND e.isActive = true",
            countQuery = "SELECT COUNT(e) FROM Product e WHERE (LOWER(e.name) LIKE %:name% OR CAST(e.id AS string) LIKE %:id%) AND e.isActive = true")
    Page<Product> findByNameContainingIgnoreCaseOrIdAndIsActive(@Param("name") String name, @Param("id") Long id, Pageable pageable);
    Optional<Product> findByName(String name);
    Page<Product> findAllByIsActive(Boolean isActive, Pageable pageable);
    Optional<Product> findByIdAndIsActive(Long id, Boolean isActive);
    @Query(value = "SELECT * FROM product as p WHERE p.is_active = 1 ORDER BY RAND() LIMIT 12", nativeQuery = true)
    List<Product> findRandomProducts();
}
