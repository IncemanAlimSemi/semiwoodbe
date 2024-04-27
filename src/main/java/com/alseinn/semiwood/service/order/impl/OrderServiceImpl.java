package com.alseinn.semiwood.service.order.impl;

import com.alseinn.semiwood.dao.cart.CartRepository;
import com.alseinn.semiwood.dao.order.OrderRepository;
import com.alseinn.semiwood.dao.product.ProductRepository;
import com.alseinn.semiwood.dao.user.UserRepository;
import com.alseinn.semiwood.entity.cart.Cart;
import com.alseinn.semiwood.entity.cart.CartItem;
import com.alseinn.semiwood.entity.order.Order;
import com.alseinn.semiwood.entity.order.OrderItem;
import com.alseinn.semiwood.entity.order.enums.Status;
import com.alseinn.semiwood.entity.product.Product;
import com.alseinn.semiwood.entity.user.User;
import com.alseinn.semiwood.request.order.CreateOrderRequest;
import com.alseinn.semiwood.request.order.OrderItemDetail;
import com.alseinn.semiwood.request.order.UpdateOrderStatusRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.order.OrderDetail;
import com.alseinn.semiwood.response.order.OrderDetailResponse;
import com.alseinn.semiwood.response.order.OrderInformationResponse;
import com.alseinn.semiwood.response.order.PageableOrderResponse;
import com.alseinn.semiwood.service.email.EmailService;
import com.alseinn.semiwood.service.order.OrderService;
import com.alseinn.semiwood.utils.ResponseUtils;
import com.alseinn.semiwood.utils.UserUtils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final ResponseUtils responseUtils;
    private final UserUtils userUtils;
    private final EmailService emailService;

    private static final Logger LOG = Logger.getLogger(OrderServiceImpl.class.getName());

    @Override
    @Transactional
    public OrderInformationResponse createOrder(CreateOrderRequest request) {
        try {
            User sessionUser = userUtils.getUserFromSecurityContext();
            Order order;

            if (Objects.nonNull(sessionUser)) {
                order = createOrderFromCart(sessionUser, null, null);
            } else if (StringUtils.isNotBlank(request.getEmail())) {
                if (Objects.isNull(request.getCartId())) {
                    LOG.info(responseUtils.getMessage("cart.id.empty") + "- Request: " + request);
                    return createOrderInformationResponse(false, responseUtils.getMessage("order.create.error"), null);
                }

                String email = request.getEmail();
                Optional<User> optionalUser = userRepository.findByEmail(email);

                if (optionalUser.isPresent()) {
                    LOG.info(responseUtils.getMessage("account.exists") + "- Request: " + request);
                    return createOrderInformationResponse(false, responseUtils.getMessage("account.exists"), null);
                }

                order = createOrderFromCart(null, email, request.getCartId());
            } else {
                LOG.info(responseUtils.getMessage("credentials.mismatch") + "- Request: " + request);
                return createOrderInformationResponse(false, responseUtils.getMessage("credentials.mismatch"), null);
            }

            if (Objects.isNull(order)) {
                LOG.warning(responseUtils.getMessage("order.create.error") + "- Request: " + request);
                return createOrderInformationResponse(false, responseUtils.getMessage("order.create.error"), null);
            }

            Order savedOrder = orderRepository.save(order);
            emailService.sendPurchaseEmail(order);

            LOG.info(responseUtils.getMessage("order.create.success") + "- Request: " + request);
            return createOrderInformationResponse(true, responseUtils.getMessage("order.create.success"), savedOrder.getId());
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("order.create.error") + "- Request: " + request + "- Exception: " + e.getMessage());
            return createOrderInformationResponse(false, responseUtils.getMessage("order.create.error"), null);
        }
    }

    @Override
    public GeneralInformationResponse updateOrderStatus(UpdateOrderStatusRequest request) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Order Id: " + request.getId());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("invalid.request"));
            }

            Optional<Order> optionalOrder = orderRepository.findById(request.getId());

            if (optionalOrder.isEmpty()) {
                LOG.info(responseUtils.getMessage("order.not.found") + "- Order Id: " + request.getId());
                return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("order.not.found"));
            }

            Order order = optionalOrder.get();
            order.setStatus(request.getStatus());

            orderRepository.save(order);

            LOG.info(responseUtils.getMessage("order.status.update.success") + "- Order Id: " + request.getId());
            return responseUtils.createGeneralInformationResponse(true, responseUtils.getMessage("order.status.update.success"));
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("order.status.update.error") + "- Order Id: " + request.getId() + "- Exception: " + e.getMessage());
            return responseUtils.createGeneralInformationResponse(false, responseUtils.getMessage("order.status.update.error"));
        }
    }

    @Override
    public PageableOrderResponse getAnonymousUsersOrders(Pageable pageable) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + pageable);
                return createPageableOrderResponse(false, responseUtils.getMessage("invalid.permission"), null);
            }

            Page<Order> orders = orderRepository.findAllByUserIsNull(pageable);

            if (orders.isEmpty()) {
                LOG.info(responseUtils.getMessage("order.not.found") + "- Request: " + pageable);
                return createPageableOrderResponse(false, responseUtils.getMessage("order.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("guest.orders.fetch.success") + "- Request: " + pageable);
            return createPageableOrderResponse(true, responseUtils.getMessage("guest.orders.fetch.success"), orders);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("guest.orders.fetch.error") + "- Request: " + pageable + "- Exception: " + e.getMessage());
            return createPageableOrderResponse(false, responseUtils.getMessage("guest.orders.fetch.error"), null);
        }
    }

    @Override
    public PageableOrderResponse getRegisteredUsersOrders(Pageable pageable) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Request: " + pageable);
                return createPageableOrderResponse(false, responseUtils.getMessage("invalid.request"), null);
            }

            Page<Order> orders = orderRepository.findAllByEmailIsNull(pageable);

            if (orders.isEmpty()) {
                LOG.info(responseUtils.getMessage("order.not.found") + "- Request: " + pageable);
                return createPageableOrderResponse(false, responseUtils.getMessage("order.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("registered.users.orders.fetch.success") + "- Request: " + pageable);
            return createPageableOrderResponse(true, responseUtils.getMessage("registered.users.orders.fetch.success"), orders);
        } catch (Exception e) {
            LOG.warning(responseUtils.getMessage("registered.users.orders.fetch.error") + "- Request: " + pageable + "- Exception: " + e.getMessage());
            return createPageableOrderResponse(false, responseUtils.getMessage("registered.users.orders.fetch.error"), null);
        }
    }

    @Override
    public PageableOrderResponse getOrdersBySessionUser(Pageable pageable) {
        try {
            User user = userUtils.getUserFromSecurityContext();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("user.not.found") + "- Request: " + pageable);
                return createPageableOrderResponse(false, responseUtils.getMessage("user.not.found"), null);
            }

            Page<Order> orders = orderRepository.findOrdersByUser(pageable, user);

            if (orders.isEmpty()) {
                LOG.info(responseUtils.getMessage("order.not.found") + "- Request: " + pageable);
                return createPageableOrderResponse(false, responseUtils.getMessage("order.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("orders.fetch.success") + "- Request: " + pageable);
            return createPageableOrderResponse(true, responseUtils.getMessage("orders.fetch.success"), orders);
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("orders.fetch.error") + "- Request: " + pageable + "- Exception: " + e.getMessage());
            return createPageableOrderResponse(false, responseUtils.getMessage("orders.fetch.error"), null);
        }
    }

    @Override
    public OrderDetailResponse getOrderById(Long id) {
        try {
            User user = userUtils.getUserIfUserRoleAdmin();

            if (Objects.isNull(user)) {
                LOG.info(responseUtils.getMessage("invalid.permission") + "- Order Id: " + id);
                return createOrderDetailResponse(false, responseUtils.getMessage("invalid.request"), null);
            }

            Optional<Order> order = orderRepository.findById(id);

            if (order.isEmpty()) {
                LOG.info(responseUtils.getMessage("order.not.found") + "- Order Id: " + id);
                return createOrderDetailResponse(false, responseUtils.getMessage("order.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("order.fetch.success") + "- Order Id: " + id);
            return createOrderDetailResponse(true, responseUtils.getMessage("order.fetch.success"), order.get());
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("order.fetch.error") + "- Order Id: " + id + "- Exception: " + e.getMessage());
            return createOrderDetailResponse(false, responseUtils.getMessage("order.fetch.error"), null);
        }
    }

    @Override
    public OrderDetailResponse getOrderByIdAndEmail(Long id, String email) {
        try {
            Optional<Order> order = orderRepository.findByIdAndEmail(id, email);

            if (order.isEmpty()) {
                LOG.info(responseUtils.getMessage("order.not.found") + "- Order Id: " + id + "- User: " + email);
                return createOrderDetailResponse(false, responseUtils.getMessage("order.not.found"), null);
            }

            LOG.info(responseUtils.getMessage("order.fetch.success") + "- Order Id: " + id + "- User: " + email);
            return createOrderDetailResponse(true, responseUtils.getMessage("order.fetch.success"), order.get());
        } catch (Exception e) {
            LOG.info(responseUtils.getMessage("order.fetch.error") + "- Order Id: " + id + "- User: " + email + "- Exception: " + e.getMessage());
            return createOrderDetailResponse(false, responseUtils.getMessage("order.fetch.error"), null);
        }
    }

    private Order createOrderFromCart(User user, String email, Long cartId) {
        Optional<Cart> optionalCart;
        if (Objects.isNull(user)) {
            optionalCart = cartRepository.findById(cartId);
        } else {
            optionalCart = cartRepository.findByUser(user);
        }

        if (optionalCart.isEmpty()) {
            LOG.info(responseUtils.getMessage("user.cart.not.found") + "- User: " + user.getEmail() + "- Anonymous User: " + email);
            return null;
        }

        return createOrderFromCart(optionalCart.get(), user, email);
    }

    private Order createOrderFromCart(Cart cart, User user, String email) {
        if (Objects.isNull(cart) || (Objects.isNull(user) && StringUtils.isBlank(email))) {
            return null;
        }

        Order order = Order.builder()
                .date(new Date())
                .status(Status.NEW)
                .user(user)
                .email(email)
                .totalPrice(cart.getTotalPrice())
                .timeCreated(new Date())
                .timeModified(new Date())
                .build();

        createOrderItemsFromCart(order, cart.getCartItems(), user, email);

        return order;
    }

    private void createOrderItemsFromCart(Order order, Set<CartItem> cartItems, User user, String email) {
        if (CollectionUtils.isEmpty(cartItems) || (Objects.isNull(user) && StringUtils.isBlank(email))) {
            return;
        }

        Set<OrderItem> orderItems = new HashSet<>();

        cartItems.forEach(cartItem ->
                orderItems.add(
                        OrderItem.builder()
                                .order(order)
                                .product(cartItem.getProduct())
                                .quantity(cartItem.getQuantity())
                                .user(user)
                                .email(email)
                                .totalPrice(cartItem.getTotalPrice())
                                .timeCreated(new Date())
                                .timeModified(new Date())
                                .build()
                )
        );

        order.setOrderItems(orderItems);
    }

    @Deprecated(forRemoval = true)
    private Set<com.alseinn.semiwood.entity.order.OrderItem> createOrderItemSet(List<OrderItemDetail> orderItemList, User user, String email) {
        List<com.alseinn.semiwood.entity.order.OrderItem> orderItems = new ArrayList<>();
        List<Long> productIdList = orderItemList.stream().map(OrderItemDetail::getProductId).toList();
        List<Product> products = productRepository.findAllById(productIdList);

        if (products.isEmpty() || !Objects.equals(productIdList.size(), products.size())) {
            return new HashSet<>();
        }

        products.forEach(product -> {
            OrderItemDetail orderItemDetail = orderItemList.stream().filter(item -> item.getProductId().equals(product.getId())).findFirst().get();
            orderItems.add(
                    com.alseinn.semiwood.entity.order.OrderItem.builder()
                            .product(product)
                            .quantity(orderItemDetail.getQuantity())
                            .user(user)
                            .email(email)
                            .timeCreated(new Date())
                            .timeModified(new Date())
                            .totalPrice(orderItemDetail.getQuantity() * product.getPrice())
                            .build()
            );

            product.setStock(product.getStock() - orderItemDetail.getQuantity());
        });

        return new HashSet<>(orderItems);
    }

    @Deprecated(forRemoval = true)
    private OrderInformationResponse createProductNotFoundMessage(CreateOrderRequest request) {
        LOG.info(responseUtils.getMessage("product.not.found") + "- Request: " + request);
        return createOrderInformationResponse(false, responseUtils.getMessage("product.not.found"), null);
    }

    @Deprecated(forRemoval = true)
    private Order createOrderModel(Set<com.alseinn.semiwood.entity.order.OrderItem> orderItems, User user, String email) {
        return Order.builder()
                .orderItems(orderItems)
                .date(new Date())
                .status(Status.NEW)
                .user(user)
                .email(email)
                .timeCreated(new Date())
                .timeModified(new Date())
                .totalPrice(orderItems.stream().mapToDouble(com.alseinn.semiwood.entity.order.OrderItem::getTotalPrice).sum())
                .build();
    }

    private OrderInformationResponse createOrderInformationResponse(boolean isSuccess, String message, Long id) {
        return OrderInformationResponse.builder()
                .id(id)
                .isSuccess(isSuccess)
                .message(message)
                .build();
    }

    private PageableOrderResponse createPageableOrderResponse(Boolean isSuccess, String message, Page<Order> orders) {
        return PageableOrderResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .orders(Objects.nonNull(orders) ? createPageableOrderDetail(orders) : null)
                .build();
    }

    private Page<OrderDetail> createPageableOrderDetail(Page<Order> orders) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return orders.map(order ->
                OrderDetail.builder()
                        .id(order.getId())
                        .email(Objects.nonNull(order.getUser()) ? order.getUser().getEmail() : order.getEmail())
                        .status(order.getStatus().toString())
                        .date(dateFormat.format(order.getDate()))
                        .orderItems(createOrderItemDetailSet(order))
                        .totalPrice(order.getTotalPrice())
                        .build()
        );
    }

    private Set<com.alseinn.semiwood.response.order.OrderItemDetail> createOrderItemDetailSet(Order order) {
        List<com.alseinn.semiwood.response.order.OrderItemDetail> orderItemDetailResponses = new ArrayList<>();
        Set<com.alseinn.semiwood.entity.order.OrderItem> orderItems = order.getOrderItems();
        orderItems.forEach(orderItem -> orderItemDetailResponses.add(
                com.alseinn.semiwood.response.order.OrderItemDetail.builder()
                        .name(orderItem.getProduct().getName())
                        .quantity(orderItem.getQuantity())
                        .price(orderItem.getProduct().getPrice())
                        .build()
        ));

        return new HashSet<>(orderItemDetailResponses);
    }

    private OrderDetailResponse createOrderDetailResponse(Boolean isSuccess, String message, Order order) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return OrderDetailResponse.builder()
                .isSuccess(isSuccess)
                .message(message)
                .order(Objects.nonNull(order) ? OrderDetail.builder()
                        .id(order.getId())
                        .email(Objects.nonNull(order.getUser()) ? order.getUser().getEmail() : order.getEmail())
                        .date(dateFormat.format(order.getDate()))
                        .status(order.getStatus().toString())
                        .totalPrice(order.getTotalPrice())
                        .build() : null)
                .build();
    }
}