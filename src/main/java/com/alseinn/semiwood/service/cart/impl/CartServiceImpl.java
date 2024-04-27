package com.alseinn.semiwood.service.cart.impl;

import com.alseinn.semiwood.dao.cart.CartRepository;
import com.alseinn.semiwood.dao.product.ProductRepository;
import com.alseinn.semiwood.entity.cart.Cart;
import com.alseinn.semiwood.entity.cart.CartItem;
import com.alseinn.semiwood.entity.product.Product;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.request.cart.AddToCartRequest;
import com.alseinn.semiwood.request.cart.CartItemDetail;
import com.alseinn.semiwood.request.cart.CreateCartRequest;
import com.alseinn.semiwood.response.cart.CartDetail;
import com.alseinn.semiwood.response.cart.CartDetailResponse;
import com.alseinn.semiwood.response.cart.CartInformationResponse;
import com.alseinn.semiwood.service.cart.CartService;
import com.alseinn.semiwood.utils.ResponseUtils;
import com.alseinn.semiwood.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserUtils userUtils;
    private final ResponseUtils responseUtils;

    private static final Logger LOG = Logger.getLogger(CartServiceImpl.class.getName());

    @Override
    public CartInformationResponse createCart(CreateCartRequest request) {
        try {
            User user = userUtils.getUserFromSecurityContext();
            Cart cart;

            if (Objects.nonNull(user)) {
                cartRepository.deleteByUser(user);
            }

            cart = createCartModel(request.getItem(), user);

            if (Objects.isNull(cart) || CollectionUtils.isEmpty(cart.getCartItems())) {
                LOG.info(responseUtils.getMessage("cart.creation.error") + "- Request: " + request);
                return getFailureCartInformationResponse();
            }

            LOG.info(responseUtils.getMessage("product.added.to.cart.success") + "- Request" + request + "- User: " + user.getEmail());
            return getSuccessCartInformationResponse(cartRepository.save(cart));
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("product.added.to.cart.error") + "- Request: " + request + "- Exception: " + e);
            return getErrorCartInformationResponse();
        }
    }

    @Override
    public CartInformationResponse addToCart(AddToCartRequest request) {
        try {
            User user = userUtils.getUserFromSecurityContext();
            Optional<Cart> optionalCart;

            if (Objects.nonNull(user)) {
                optionalCart = cartRepository.findByUser(user);
            } else {
                optionalCart = cartRepository.findById(request.getId());
            }

            if (optionalCart.isEmpty()) {
                LOG.info(responseUtils.getMessage("user.cart.not.found") + "- Request: " + request);
                return getFailureCartInformationResponse();
            }

            Cart cart = optionalCart.get();
            Optional<Product> optionalProduct = productRepository.findById(request.getProductId());

            if (optionalProduct.isEmpty()) {
                LOG.info(responseUtils.getMessage("product.not.found") + "- Request: " + request);
                return getFailureCartInformationResponse();
            }

            Product product = optionalProduct.get();
            Optional<CartItem> optionalCartItem = cart.getCartItems().stream().filter(item -> Objects.equals(item.getId(), product.getId())).findFirst();
            int quantity;

            if (optionalCartItem.isEmpty()) {
                quantity = checkStock(product.getStock(), request.getQuantity());

                if (Objects.equals(0, quantity)) {
                    LOG.info(responseUtils.getMessage("product.stock.not.available") + "- Request: " + request);
                    return getFailureCartInformationResponse();
                }

                createCartItem(cart, product, quantity);
            } else {
                CartItem cartItem = optionalCartItem.get();

                quantity = checkStock(product.getStock() + cartItem.getQuantity(), request.getQuantity());

                if (Objects.equals(0, quantity)) {
                    LOG.info(responseUtils.getMessage("product.stock.not.available") + "- Request: " + request);
                    return getFailureCartInformationResponse();
                }

                cartItem.setQuantity(quantity);
            }

            cartRepository.save(cart);

            LOG.info(responseUtils.getMessage("product.added.to.cart.success") + "- Request: " + request);
            return getSuccessCartInformationResponse(cart);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("product.added.to.cart.error") + "- Request: " + request + "- Exception: " + e);
            return getErrorCartInformationResponse();
        }
    }

    @Override
    public CartInformationResponse deleteCartById(Long id) {
        try {
            User user = userUtils.getUserFromSecurityContext();

            if (Objects.nonNull(user)) {
                cartRepository.deleteByUser(user);
            } else {
                Optional<Cart> optionalCart = cartRepository.findById(id);

                if (optionalCart.isEmpty() || Objects.nonNull(optionalCart.get().getUser())) {
                    LOG.info(responseUtils.getMessage("user.cart.not.found") + "- Cart Id: " + id);
                    return createCartInformationResponse(true, responseUtils.getMessage("user.cart.not.found"), null);
                }

                cartRepository.deleteById(id);
            }

            LOG.info(responseUtils.getMessage("cart.delete.success") + "- User: " + user.getEmail() + "- Cart Id: " + id);
            return createCartInformationResponse(true, responseUtils.getMessage("cart.delete.success"), null);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("cart.delete.error") + "- Cart Id: " + id);
            return createCartInformationResponse(true, responseUtils.getMessage("cart.delete.error"), null);
        }
    }

    @Override
    public CartDetailResponse getCartById(Long id) {
        try {
            User user = userUtils.getUserFromSecurityContext();
            Optional<Cart> optionalCart;

            if (Objects.nonNull(user)) {
                optionalCart = cartRepository.findByUser(user);
            } else {
                optionalCart = cartRepository.findById(id);
            }

            if (optionalCart.isEmpty()) {
                LOG.info(responseUtils.getMessage("user.cart.not.found") + "- User: " + user.getEmail() + "- Cart Id: " + id);
                return createCartDetailResponse(false, responseUtils.getMessage("user.cart.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("cart.fetched.success") + "- User: " + user.getEmail() + "- Cart Id: " + id);
            return createCartDetailResponse(true, responseUtils.getMessage("cart.fetched.success"), optionalCart.get());
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("cart.fetch.error") + "- Cart Id: " + id + "- Exception: " + e);
            return createCartDetailResponse(false, responseUtils.getMessage("cart.fetch.error"), null);
        }
    }

    private CartInformationResponse getFailureCartInformationResponse() {
        return createCartInformationResponse(false, responseUtils.getMessage("product.added.to.cart.success"), null);
    }

    private CartInformationResponse getSuccessCartInformationResponse(Cart cart) {
        return createCartInformationResponse(true, responseUtils.getMessage("product.added.to.cart.success"),
                Objects.nonNull(cart) ? cart.getId() : null);
    }

    private CartInformationResponse getErrorCartInformationResponse() {
        return createCartInformationResponse(false, responseUtils.getMessage("product.added.to.cart.success"), null);
    }

    private Cart createCartModel(List<CartItemDetail> cartItems, User user) {
        if (CollectionUtils.isEmpty(cartItems)) {
            return null;
        }

        Cart cart = Cart.builder()
                .user(user)
                .timeCreated(new Date())
                .timeModified(new Date())
                .build();

        createCartItemSet(cart, cartItems);

        return cart;
    }

    private void createCartItem(Cart cart, Product product, int quantity) {
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .totalPrice(quantity * product.getPrice())
                .timeCreated(new Date())
                .timeModified(new Date())
                .build();

        cart.getCartItems().add(cartItem);
        calculateCartTotal(cart);
    }

    private void createCartItemSet(Cart cart, List<CartItemDetail> cartItemDetailList) {
        Set<CartItem> cartItems = new HashSet<>();

        if (Objects.isNull(cart) || cartItemDetailList.isEmpty()) {
            return;
        }

        Map<Long, CartItemDetail> productsMap = cartItemDetailList.stream().collect(Collectors.toMap(CartItemDetail::getProductId, cartItemDetail -> cartItemDetail));
        List<Product> products = productRepository.findAllById(productsMap.keySet());

        if (products.isEmpty()) {
            return;
        }

        products.forEach(product -> {
            CartItemDetail cartItemDetail = productsMap.get(product.getId());

            int quantity = checkStock(product.getStock(), cartItemDetail.getQuantity());

            if (quantity != 0) {
                cartItems.add(
                        CartItem.builder()
                                .cart(cart)
                                .product(product)
                                .quantity(quantity)
                                .totalPrice(quantity * product.getPrice())
                                .timeCreated(new Date())
                                .timeModified(new Date())
                                .build()
                );

                product.setStock(product.getStock() - quantity);
            }
        });

        cart.getCartItems().addAll(cartItems);
    }

    private int checkStock(Integer stock, Integer quantity) {
        if (stock == 0) {
            return 0;
        } else if (stock < quantity) {
            return stock;
        }

        return quantity;
    }


    private CartInformationResponse createCartInformationResponse(Boolean isSuccess, String message, Long cartId) {
        return CartInformationResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .id(cartId)
                .build();
    }


    private CartDetailResponse createCartDetailResponse(boolean isSuccess, String message, Cart cart) {
        return CartDetailResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .cart(Objects.nonNull(cart) ? createCartDetail(cart) : null)
                .build();
    }

    private CartDetail createCartDetail(Cart cart) {
        return CartDetail.builder()
                .id(cart.getId())
                .items(createItemDetailList(cart.getCartItems()))
                .totalPrice(cart.getTotalPrice())
                .build();
    }

    private List<com.alseinn.semiwood.response.cart.CartItemDetail> createItemDetailList(Set<CartItem> cartItems) {
        List<com.alseinn.semiwood.response.cart.CartItemDetail> cartItemDetailList = new ArrayList<>();

        cartItems.forEach(cartItem -> cartItemDetailList.add(
                com.alseinn.semiwood.response.cart.CartItemDetail.builder()
                        .id(cartItem.getProduct().getId())
                        .name(cartItem.getProduct().getName())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getTotalPrice())
                        .build()
        ));

        return cartItemDetailList;
    }

    private void calculateCartTotal(Cart cart) {
        Set<CartItem> cartItems = cart.getCartItems();

        if (CollectionUtils.isEmpty(cartItems)) {
            cart.setTotalPrice(0d);
        }

        cart.setTotalPrice(cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum());
    }
}
