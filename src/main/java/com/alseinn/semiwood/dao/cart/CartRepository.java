package com.alseinn.semiwood.dao.cart;

import com.alseinn.semiwood.entity.cart.Cart;
import com.alseinn.semiwood.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
    void deleteByUser(User user);
}
