package com.alseinn.semiwood.response.working;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHoursDetail {
    private String day;
    private String startHours;
    private String endHours;
}