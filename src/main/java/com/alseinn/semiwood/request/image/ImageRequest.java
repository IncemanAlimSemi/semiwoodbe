package com.alseinn.semiwood.request.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageRequest {
    private String name;
    private String type;
    private byte[] data;
}
