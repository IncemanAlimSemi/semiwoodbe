package com.alseinn.semiwood.dao.order;

import com.alseinn.semiwood.entity.order.Order;
import com.alseinn.semiwood.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserIsNull(Pageable pageable);
    Page<Order> findAllByEmailIsNull(Pageable pageable);
    Optional<Order> findByIdAndEmail(Long id, String email);
    Page<Order> findOrdersByUser(Pageable pageable, User user);
}
