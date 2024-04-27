package com.alseinn.semiwood.response.working;

import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHoursResponse extends GeneralInformationResponse {
    private Set<WorkingHoursDetail> workings;
}
