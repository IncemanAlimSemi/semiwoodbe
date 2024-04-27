package com.alseinn.semiwood.controller.about;

import com.alseinn.semiwood.request.about.CreateOrUpdateAboutRequest;
import com.alseinn.semiwood.response.about.AboutResponse;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import com.alseinn.semiwood.service.about.AboutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${default.api.path}/about")
@RequiredArgsConstructor
public class AboutController {

    private final AboutService aboutService;
    @PostMapping()
    public GeneralInformationResponse createAbout(@RequestBody CreateOrUpdateAboutRequest request) {
        return aboutService.createAbout(request);
    }

    @PutMapping()
    public GeneralInformationResponse updateAbout(@RequestBody CreateOrUpdateAboutRequest request) {
        return aboutService.updateAbout(request);
    }

    @GetMapping()
    public AboutResponse getAbout() {
        return aboutService.getAbout();
    }
}