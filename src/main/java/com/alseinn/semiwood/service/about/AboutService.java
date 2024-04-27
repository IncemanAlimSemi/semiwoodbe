package com.alseinn.semiwood.service.about;

import com.alseinn.semiwood.request.about.CreateOrUpdateAboutRequest;
import com.alseinn.semiwood.response.about.AboutResponse;
import com.alseinn.semiwood.response.general.GeneralInformationResponse;

public interface AboutService {
    GeneralInformationResponse createAbout(CreateOrUpdateAboutRequest request);
    GeneralInformationResponse updateAbout(CreateOrUpdateAboutRequest request);
    AboutResponse getAbout();
}
