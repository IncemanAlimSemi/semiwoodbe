package com.alseinn.semiwood.service.order;

import com.alseinn.semiwood.request.order.CreateOrderRequest;
import com.alseinn.semiwood.request.order.UpdateOrderStatusRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.order.OrderDetailResponse;
import com.alseinn.semiwood.response.order.OrderInformationResponse;
import com.alseinn.semiwood.response.order.PageableOrderResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderInformationResponse createOrder(CreateOrderRequest request);
    GeneralInformationResponse updateOrderStatus(UpdateOrderStatusRequest request);
    PageableOrderResponse getAnonymousUsersOrders(Pageable pageable);
    PageableOrderResponse getRegisteredUsersOrders(Pageable pageable);
    PageableOrderResponse getOrdersBySessionUser(Pageable pageable);
    OrderDetailResponse getOrderById(Long id);
    OrderDetailResponse getOrderByIdAndEmail(Long id, String email);
}
