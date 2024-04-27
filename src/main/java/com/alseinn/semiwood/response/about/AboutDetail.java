package com.alseinn.semiwood.response.about;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AboutDetail {
    private String title;
    private String content;
    private List<String> cdnLinks;
}
