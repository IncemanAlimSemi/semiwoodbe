package com.alseinn.semiwood.request.about;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateAboutRequest {
    String title;
    String content;
    List<String> cdnLinks;
}
