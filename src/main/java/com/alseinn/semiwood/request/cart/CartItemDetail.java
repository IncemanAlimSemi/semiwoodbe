package com.alseinn.semiwood.request.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDetail {
    private Long productId;
    private Integer quantity;
}
