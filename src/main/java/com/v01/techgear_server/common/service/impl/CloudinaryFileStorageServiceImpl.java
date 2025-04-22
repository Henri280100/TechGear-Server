package com.v01.techgear_server.common.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.v01.techgear_server.common.dto.ImageDimensionsDTO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.v01.techgear_server.constant.ErrorMessageConstants;
import com.v01.techgear_server.common.dto.ImageDTO;
import com.v01.techgear_server.common.dto.MediaDTO;
import com.v01.techgear_server.enums.ImageTypes;
import com.v01.techgear_server.exception.FileUploadingException;
import com.v01.techgear_server.common.mapping.MediaMapper;
import com.v01.techgear_server.common.model.Media;
import com.v01.techgear_server.common.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryFileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;
    private final MediaMapper mediaMapper;

    private static final String CLOUDINARY_FOLDER = "techgear";
    private static final String PUBLIC_ID = "public_id";
    private static final String SECURE_URL = "secure_url";
    private static final String FOLDER = "folder";
    private static final String RESOURCE_TYPE = "resource_type";
    private static final String QUALITY = "quality";
    private static final String FETCH_FORMAT = "fetch_format";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String CROP = "crop";
    private static final String LIMIT = "limit";
    private static final String AUTO_QUALITY = "auto:good";

    @Override
    public CompletableFuture<Void> deleteImage(String publicId) {
        return CompletableFuture.runAsync(() -> {
            try {
                cloudinary.uploader()
                        .destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception e) {
                log.error(ErrorMessageConstants.ERROR_DELETING_IMAGE, e);
                throw new FileUploadingException(ErrorMessageConstants.ERROR_DELETING_IMAGE, e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteMedia(String publicId) {
        return CompletableFuture.runAsync(() -> {
            try {
                cloudinary.uploader()
                        .destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception e) {
                throw new FileUploadingException(ErrorMessageConstants.ERROR_DELETING_MEDIA, e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteMultipleImages(List<String> publicIds) {
        return CompletableFuture.runAsync(() -> {
            try {
                for (String publicId : publicIds) {
                    cloudinary.uploader()
                            .destroy(publicId, ObjectUtils.emptyMap());
                }
            } catch (Exception e) {
                log.error(ErrorMessageConstants.ERROR_DELETING_MULTI_IMAGES, e);
                throw new FileUploadingException(ErrorMessageConstants.ERROR_DELETING_MULTI_IMAGES, e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteMultipleMedia(List<String> publicIds) {
        return CompletableFuture.runAsync(() -> {
            try {
                for (String publicId : publicIds) {
                    cloudinary.uploader()
                            .destroy(publicId, ObjectUtils.emptyMap());
                }
            } catch (Exception e) {
                log.error(ErrorMessageConstants.ERROR_DELETING_MULTI_MEDIA, e);
                throw new FileUploadingException(ErrorMessageConstants.ERROR_DELETING_MULTI_MEDIA, e);
            }
        });
    }

    @Override
    @Async
    public CompletableFuture<ImageDTO> storeSingleImage(MultipartFile file) throws IOException {
        return CompletableFuture.supplyAsync(() -> uploadImage(file));
    }

    @Override
    public CompletableFuture<List<ImageDTO>> storedMultipleImage(List<MultipartFile> files) throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            List<MultipartFile> validFiles = files.stream()
                    .filter(file -> !file.isEmpty())
                    .toList();

            if (validFiles.isEmpty()) {
                throw new IllegalArgumentException("No valid files to upload");
            }

            return validFiles.parallelStream()
                    .map(this::uploadImage) // ✅ Reuse uploadImage method
                    .toList();
        });
    }

    /**
     * Helper method to handle image upload to Cloudinary and return ImageDTO.
     */
    private ImageDTO uploadImage(MultipartFile file) {
        try {
            validateFiles(file);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            FOLDER, CLOUDINARY_FOLDER,
                            RESOURCE_TYPE, "image",
                            QUALITY, AUTO_QUALITY,
                            FETCH_FORMAT, "auto",
                            WIDTH, 1024,
                            CROP, LIMIT
                    ));

            String url = (String) uploadResult.get(SECURE_URL);
            String publicId = (String) uploadResult.get(PUBLIC_ID);
            Integer width = (Integer) uploadResult.get(WIDTH);
            Integer height = (Integer) uploadResult.get(HEIGHT);

            ImageDimensionsDTO dimensions = new ImageDimensionsDTO(
                    Optional.ofNullable(width).orElse(0),
                    Optional.ofNullable(height).orElse(0)
            );

            return ImageDTO.builder()
                    .publicId(publicId)
                    .imageUrl(url)
                    .dimensions(dimensions)
                    .type(ImageTypes.GENERIC)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            throw new FileUploadingException("Unexpected error during image upload: " + e.getMessage(), e);
        }
    }


    @Override
    public MediaDTO storeMedia(MultipartFile mediaFile) throws IOException {
        try {
            validateFiles(mediaFile);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader()
                    .upload(mediaFile.getBytes(),
                            ObjectUtils.asMap(
                                    FOLDER, CLOUDINARY_FOLDER,
                                    RESOURCE_TYPE, "video",
                                    // Auto-detect resource type (image, video, etc.)
                                    QUALITY, AUTO_QUALITY,
                                    // Auto quality based on optimization
                                    FETCH_FORMAT, "auto"
                                    // Automatically optimize the format

                            ));
            String url = (String) uploadResult.get(SECURE_URL); // Use secure URL
            String publicId = (String) uploadResult.get(PUBLIC_ID);
            String contentType = mediaFile.getContentType();

            // Create Media object
            Media media = new Media();
            media.setMediaFilename(publicId);
            media.setMediaContentType(contentType);
            media.setData(url.getBytes()); // Optional: Store URL as bytes if needed

            return mediaMapper.toDTO(media);
        } catch (IOException e) {
            throw new IOException("Failed to upload media due to I/O error", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to upload media", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MediaDTO> storeMultipleMedia(List<MultipartFile> mediaFile) throws IOException {
        List<Media> uploadedMedia = new ArrayList<>();

        // Validate and filter non-empty files
        List<MultipartFile> validFiles = mediaFile.stream()
                .filter(file -> !file.isEmpty())
                .toList();

        if (validFiles.isEmpty()) {
            throw new IllegalArgumentException("No valid files to upload");
        }

        for (MultipartFile file : validFiles) {
            Map<String, Object> uploadResult = cloudinary.uploader()
                    .upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    FOLDER, CLOUDINARY_FOLDER,
                                    RESOURCE_TYPE, "auto",
                                    // Auto-detect resource type (image, video, etc.)
                                    QUALITY, AUTO_QUALITY,
                                    // Auto quality based on optimization
                                    FETCH_FORMAT, "auto",
                                    // Automatically optimize the format
                                    PUBLIC_ID, file.getOriginalFilename()
                                    // Optional: Set public ID
                            ));
            String url = (String) uploadResult.get(SECURE_URL); // Use secure URL
            String publicId = (String) uploadResult.get(PUBLIC_ID);
            String contentType = file.getContentType();

            // Create Media object
            Media media = new Media();
            media.setMediaFilename(publicId);
            media.setMediaContentType(contentType);
            media.setData(url.getBytes()); // Optional: Store URL as bytes if needed

            uploadedMedia.add(media); // Add the uploaded media to the list to be upload
        }
        return uploadedMedia.stream()
                .map(mediaMapper::toDTO)
                .toList();
    }

    private void validateFiles(MultipartFile file) throws FileUploadingException {
        if (file == null || file.isEmpty()) {
            throw new FileUploadingException("Cannot upload an empty file");
        }
    }

}
