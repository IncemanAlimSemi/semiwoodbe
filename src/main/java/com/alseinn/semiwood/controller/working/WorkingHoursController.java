package com.alseinn.semiwood.controller.working;

import com.alseinn.semiwood.request.working.WorkingHoursDetailRequest;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.response.working.WorkingHoursResponse;
import com.alseinn.semiwood.service.working.WorkingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${default.api.path}/hours")
@RequiredArgsConstructor
public class WorkingHoursController {

    private final WorkingHoursService workingHoursService;

    @PostMapping()
    GeneralInformationResponse createWorkingHours(@RequestBody WorkingHoursDetailRequest request) {
        return workingHoursService.createWorkingHours(request);
    }

    @PutMapping()
    GeneralInformationResponse updateWorkingHours(@RequestBody WorkingHoursDetailRequest request) {
        return workingHoursService.updateWorkingHours(request);
    }

    @GetMapping()
    public WorkingHoursResponse findAllWorkingHours() {
        return workingHoursService.findAllWorkingHours();
    }
}
