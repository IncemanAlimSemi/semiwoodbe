package com.alseinn.semiwood.response.general;

import com.alseinn.semiwood.response.concrete.AbstractResponse;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
public class GeneralInformationResponse extends AbstractResponse {
    private Boolean isSuccess;
    private String message;
}
