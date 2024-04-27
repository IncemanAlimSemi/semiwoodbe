package com.alseinn.semiwood.response.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDetail {
    private Long id;
    private String name;
    private Integer quantity;
    private Double price;
}
