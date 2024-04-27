package com.alseinn.semiwood.dao.cart;

import com.alseinn.semiwood.entity.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
