package com.alseinn.semiwood.request.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCartRequest {
    private Long id;
    private List<CartItemDetail> item;
}
