package com.alseinn.semiwood.dao.order;

import com.alseinn.semiwood.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
