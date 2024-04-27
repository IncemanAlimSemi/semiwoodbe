package com.alseinn.semiwood.controller.order;

import com.alseinn.semiwood.request.order.CreateOrderRequest;
import com.alseinn.semiwood.request.order.UpdateOrderStatusRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.order.OrderDetailResponse;
import com.alseinn.semiwood.response.order.OrderInformationResponse;
import com.alseinn.semiwood.response.order.PageableOrderResponse;
import com.alseinn.semiwood.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${default.api.path}/order")
@RequiredArgsConstructor
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public OrderInformationResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PutMapping("/status")
    public GeneralInformationResponse updateOrderStatus(@RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateOrderStatus(request);
    }

    @GetMapping("/anonymous")
    public PageableOrderResponse getAnonymousUsersOrders(@RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("date").descending());
        return orderService.getAnonymousUsersOrders(pageable);
    }

    @GetMapping("/registered")
    public PageableOrderResponse getRegisteredUsersOrders(@RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("date").descending());
        return orderService.getRegisteredUsersOrders(pageable);
    }

    @GetMapping("/user")
    public PageableOrderResponse getOrderBySessionUser(@RequestParam(name = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("date").descending());
        return orderService.getOrdersBySessionUser(pageable);
    }

    @GetMapping("/{id}")
    public OrderDetailResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping()
    public OrderDetailResponse getOrderByIdAndEmail(@RequestParam(name = "id") Long id, @RequestParam(name = "email") String email) {
        return orderService.getOrderByIdAndEmail(id, email);
    }

}
