package com.alseinn.semiwood.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
    private Long id;
    private String email;
    private Set<OrderItemDetail> orderItems;
    private String status;
    private String date;
    private Double totalPrice;
}
