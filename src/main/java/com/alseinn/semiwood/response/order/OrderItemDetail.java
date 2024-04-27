package com.alseinn.semiwood.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDetail {
    private String name;
    private Integer quantity;
    private Double price;
}
