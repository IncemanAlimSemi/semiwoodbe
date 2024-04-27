package com.alseinn.semiwood.response.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CartDetail {
    private Long id;
    private List<CartItemDetail> items;
    private Double totalPrice;
}
