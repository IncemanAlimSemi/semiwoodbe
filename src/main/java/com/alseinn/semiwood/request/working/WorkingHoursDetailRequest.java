package com.alseinn.semiwood.request.working;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHoursDetailRequest {
    private String day;
    private String startHours;
    private String endHours;
}
