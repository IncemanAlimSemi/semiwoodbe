package com.alseinn.semiwood.request.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    private String email;
    private Long cartId;
    private List<OrderItemDetail> item;
}