package com.alseinn.semiwood.service.storage.impl;

import com.alseinn.semiwood.dao.image.ImageRepository;
import com.alseinn.semiwood.entity.image.Image;
import com.alseinn.semiwood.request.image.ImageRequest;
import com.alseinn.semiwood.service.storage.ImageService;
import com.alseinn.semiwood.utils.ImageUtils;
import com.alseinn.semiwood.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ResponseUtils responseUtils;
    private static final Logger LOG = Logger.getLogger(ImageServiceImpl.class.getName());

    private static final String BASE_GOOGLE_ID = "https://lh3.google.com/u/0/d/";

    @Override
    public Image uploadImage(MultipartFile file) {
        if (Objects.nonNull(file) && !file.isEmpty()) {
            if (!isImage(file.getContentType())) {
                LOG.warning(responseUtils.getMessage("not.an.image", file.getOriginalFilename()));
                return null;
            }
            try{
                return imageRepository.save(Image.builder()
                        .name(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())))
                        .type(file.getContentType())
                        .imageData(ImageUtils.compressImage(file.getBytes()))
                        .timeCreated(new Date(System.currentTimeMillis()))
                        .timeModified(new Date(System.currentTimeMillis()))
                        .build());
            }catch (Exception e) {
                LOG.warning(MessageFormat.format("Error occurred while uploading image: {0} : {1}"
                        , file.getOriginalFilename(), e.getMessage()));

                return null;
            }
        }

        return null;
    }

    @Override
    public Image uploadImage(ImageRequest request) {
        if (Objects.nonNull(request)) {
            if (!isImage(request.getType())) {
                LOG.warning(responseUtils.getMessage("not.an.image", request.getName()));
                return null;
            }
            try{
                return imageRepository.save(Image.builder()
                        .name(StringUtils.cleanPath(Objects.requireNonNull(request.getName())))
                        .type(request.getType())
                        .imageData(ImageUtils.compressImage(request.getData()))
                        .timeCreated(new Date(System.currentTimeMillis()))
                        .timeModified(new Date(System.currentTimeMillis()))
                        .build());
            }catch (Exception e) {
                LOG.warning(MessageFormat.format("Error occurred while uploading image: {0} : {1}"
                        , request.getName(), e.getMessage()));

                return null;
            }
        }

        return null;
    }

    @Override
    public byte[] getImage(Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (Objects.isNull(image)) {
            return null;
        }
        return ImageUtils.decompressImage(image.getImageData());
    }

    @Override
    public void deleteImage(Image image) {
        if (Objects.nonNull(image)){
            try {
                imageRepository.deleteById(image.getId());
            } catch (Exception e) {
                LOG.warning("Error occurred while deleting image: " + e);
            }
        }
    }

    @Override
    public String changeUrl(String url) {
        String[] arr = url.split("/");
        String id = BASE_GOOGLE_ID;
        for (int i = 2; i <= arr.length; i++) {
            if (arr[i].startsWith("1") && arr[i].length() > 16) {
                id += arr[i];
                break;
            }
        }

        return id;
    }

    @Override
    public List<String> changeUrl(List<String> url) {
        List<String> newUrls = new ArrayList<>();
        url.forEach(u -> newUrls.add(changeUrl(u)));
        return newUrls;
    }

    private boolean isImage(String contentType) {
        return Objects.nonNull(contentType) && contentType.startsWith("image/");
    }

}
