package com.alseinn.semiwood.service.email;

import com.alseinn.semiwood.entity.order.Order;

public interface EmailService {
    void sendRegisterEmail(String email, String fullname);
    void sendPurchaseEmail(Order order);
}
