package com.alseinn.semiwood.service.cart;

import com.alseinn.semiwood.request.cart.AddToCartRequest;
import com.alseinn.semiwood.request.cart.CreateCartRequest;
import com.alseinn.semiwood.response.cart.CartDetailResponse;
import com.alseinn.semiwood.response.cart.CartInformationResponse;

public interface CartService {
    CartInformationResponse createCart(CreateCartRequest request);
    CartInformationResponse addToCart(AddToCartRequest request);
    CartInformationResponse deleteCartById(Long id);
    CartDetailResponse getCartById(Long id);
}
