package com.alseinn.semiwood.service.working;

import com.alseinn.semiwood.request.working.WorkingHoursDetailRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.working.WorkingHoursResponse;

public interface WorkingHoursService {
    GeneralInformationResponse createWorkingHours(WorkingHoursDetailRequest request);
    GeneralInformationResponse updateWorkingHours(WorkingHoursDetailRequest request);
    WorkingHoursResponse findAllWorkingHours();

}
