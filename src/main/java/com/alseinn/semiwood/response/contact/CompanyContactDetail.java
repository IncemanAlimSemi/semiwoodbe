package com.alseinn.semiwood.response.contact;

import com.alseinn.semiwood.response.working.WorkingHoursDetail;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyContactDetail {
    private String name;
    private String email;
    private String address;
    private String phoneNumber;
    private String instagram;
    private String linkedin;
    private String twitter;
    private String facebook;
    private List<WorkingHoursDetail> workingHoursDetail;
}