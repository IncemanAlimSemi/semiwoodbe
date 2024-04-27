package com.alseinn.semiwood.request.order;

import com.alseinn.semiwood.entity.order.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderStatusRequest {
    private Long id;
    private Status status;
}
