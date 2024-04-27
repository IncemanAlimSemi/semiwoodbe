package com.alseinn.semiwood.request.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequest {
    private Long id;
    private Long productId;
    private Integer quantity;
}
