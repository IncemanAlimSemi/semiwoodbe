package com.alseinn.semiwood.controller.cart;

import com.alseinn.semiwood.request.cart.AddToCartRequest;
import com.alseinn.semiwood.request.cart.CreateCartRequest;
import com.alseinn.semiwood.response.cart.CartDetailResponse;
import com.alseinn.semiwood.response.cart.CartInformationResponse;
import com.alseinn.semiwood.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${default.api.path}/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public CartInformationResponse createCart(@RequestBody CreateCartRequest request) {
        return cartService.createCart(request);
    }

    @PutMapping
    CartInformationResponse addToCart(@RequestBody AddToCartRequest request) {
        return cartService.addToCart(request);
    }

    @DeleteMapping("/{id}")
    CartInformationResponse deleteCartById(@PathVariable Long id) {
        return cartService.deleteCartById(id);
    }

    @GetMapping("/{id}")
    public CartDetailResponse getCartById(@PathVariable(name = "id") Long id) {
        return cartService.getCartById(id);
    }
}
