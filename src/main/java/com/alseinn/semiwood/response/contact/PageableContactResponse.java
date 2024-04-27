package com.alseinn.semiwood.response.contact;

import com.alseinn.semiwood.response.general.GeneralInformationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PageableContactResponse extends GeneralInformationResponse {
    Page<ContactDetail> contacts;
}
