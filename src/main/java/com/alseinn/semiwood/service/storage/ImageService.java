package com.alseinn.semiwood.service.storage;

import com.alseinn.semiwood.entity.image.Image;
import com.alseinn.semiwood.request.image.ImageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    Image uploadImage(MultipartFile file) throws IOException;
    Image uploadImage(ImageRequest request);
    byte[] getImage(Long id) throws IOException;
    void deleteImage(Image image) throws IOException;
    String changeUrl(String url);
    List<String> changeUrl(List<String> url);
}
