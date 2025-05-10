package com.v01.techgear_server.shared.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.v01.techgear_server.shared.dto.ImageDTO;
import com.v01.techgear_server.shared.dto.ImageDimensionsDTO;
import com.v01.techgear_server.shared.dto.MediaDTO;
import com.v01.techgear_server.shared.mapping.MediaMapper;
import com.v01.techgear_server.shared.model.Media;
import com.v01.techgear_server.shared.service.FileStorageService;
import com.v01.techgear_server.constant.ErrorMessageConstants;
import com.v01.techgear_server.enums.ImageTypes;
import com.v01.techgear_server.exception.FileUploadingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryFileStorageServiceImpl implements FileStorageService {

    private static final String CLOUDINARY_FOLDER = "techgear";
    private static final String PUBLIC_ID = "public_id";
    private static final String SECURE_URL = "secure_url";
    private static final String FOLDER = "folder";
    private static final String RESOURCE_TYPE = "resource_type";
    private static final String QUALITY = "quality";
    private static final String FETCH_FORMAT = "fetch_format";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String AUTO_QUALITY = "auto:good";
    private final Cloudinary cloudinary;
    private final MediaMapper mediaMapper;

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

    private byte[] compressImage(MultipartFile image, int targetWidth) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
        BufferedImage resized = Scalr.resize(bufferedImage, targetWidth);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resized, "jpg", baos);
        return baos.toByteArray();
    }

    @Override
    @Async
    public CompletableFuture<ImageDTO> storeSingleImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("File is null or empty"));
        }
        try {
            byte[] fileBytes = file.getBytes(); // Read bytes in the main thread
            return CompletableFuture.supplyAsync(() -> uploadImageBytes(fileBytes));
        } catch (IOException e) {
            log.error("Failed to read file: {}", file.getOriginalFilename(), e);
            return CompletableFuture.failedFuture(new FileUploadingException("Failed to read file", e));
        }
    }

    @Override
    @Async
    public CompletableFuture<List<ImageDTO>> storedMultipleImage(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        List<byte[]> imageBytesList;
        try {
            imageBytesList = images.stream()
                    .map(file -> {
                        try {
                            return file.getBytes();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to read file: " + file.getOriginalFilename(), e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.supplyAsync(() -> imageBytesList.stream()
                .map(this::uploadImageBytes)
                .collect(Collectors.toList()));
    }

    /**
     * Helper method to handle image upload to Cloudinary and return ImageDTO.
     */
    private ImageDTO uploadImageBytes(byte[] fileBytes) {
        try {
            validateFileContent(fileBytes); // Optional content validation
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(fileBytes,
                    ObjectUtils.asMap(
                            FOLDER, CLOUDINARY_FOLDER,
                            RESOURCE_TYPE, "image",
                            QUALITY, AUTO_QUALITY,
                            FETCH_FORMAT, "auto"
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
            throw new FileUploadingException("Failed to upload image", e);
        }
    }


    @Override
    @Async
    public CompletableFuture<MediaDTO> storeMedia(MultipartFile mediaFile) {
        if (mediaFile == null || mediaFile.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("File is null or empty"));
        }
        try {
            validateFile(mediaFile);
            byte[] fileBytes = mediaFile.getBytes();
            log.debug("Read {} bytes for file: {}", fileBytes.length, mediaFile.getOriginalFilename());
            return CompletableFuture.supplyAsync(() -> uploadMedia(fileBytes, mediaFile.getContentType()));
        } catch (IOException e) {
            log.error("Failed to read file: {}", mediaFile.getOriginalFilename(), e);
            return CompletableFuture.failedFuture(new FileUploadingException("Failed to read file", e));
        }
    }

    @Override
    @Async
    public CompletableFuture<List<MediaDTO>> storeMultipleMedia(List<MultipartFile> mediaFiles) {
        if (mediaFiles == null || mediaFiles.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<byte[]> fileBytesList;
        List<String> contentTypes;
        try {
            // Filter and validate files, cache bytes
            List<MultipartFile> validFiles = mediaFiles.stream()
                    .filter(file -> !file.isEmpty())
                    .toList();
            if (validFiles.isEmpty()) {
                log.info("No valid files to upload");
                return CompletableFuture.completedFuture(Collections.emptyList());
            }

            fileBytesList = new ArrayList<>();
            contentTypes = new ArrayList<>();
            for (MultipartFile file : validFiles) {
                validateFile(file);
                byte[] bytes = file.getBytes();
                fileBytesList.add(bytes);
                contentTypes.add(file.getContentType());
                log.debug("Read {} bytes for file: {}", bytes.length, file.getOriginalFilename());
            }
        } catch (IOException e) {
            log.error("Failed to read files", e);
            return CompletableFuture.failedFuture(new FileUploadingException("Failed to read files", e));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                return IntStream.range(0, fileBytesList.size())
                        .mapToObj(i -> uploadMedia(fileBytesList.get(i), contentTypes.get(i)))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Failed to upload multiple media files", e);
                throw new FileUploadingException("Failed to upload multiple media files", e);
            }
        });
    }

    private MediaDTO uploadMedia(byte[] mediaFile, String contentType) {
        try {
            validateFileContent(mediaFile);

            String resourceType = determineResourceType(contentType);
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(mediaFile,
                    ObjectUtils.asMap(
                            "folder", CLOUDINARY_FOLDER,
                            "resource_type", resourceType,
                            "quality", AUTO_QUALITY,
                            "fetch_format", "auto"
                    ));

            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            Media media = new Media();
            media.setMediaFilename(publicId);
            media.setMediaUrl(url); // Store URL as string, not bytes

            return mediaMapper.toDTO(media);
        } catch (Exception e) {
            log.error("Failed to upload media", e);
            throw new FileUploadingException("Failed to upload media", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > 50 * 1024 * 1024) { // 50MB limit
            throw new IllegalArgumentException("File size exceeds 50MB: " + file.getOriginalFilename());
        }
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.startsWith("image/") || contentType.startsWith("video/"))) {
            throw new IllegalArgumentException("File must be an image or video: " + file.getOriginalFilename());
        }
    }

    private void validateFileContent(byte[] fileBytes) {
        // Example: Basic content validation
        if (fileBytes.length == 0) {
            throw new IllegalArgumentException("File content is empty");
        }
        // Add specific validation (e.g., check image/video format using libraries like ImageIO or FFmpeg)
    }

    private String determineResourceType(String contentType) {
        if (contentType == null) {
            return "auto";
        }
        if (contentType.startsWith("image/")) {
            return "image";
        } else if (contentType.startsWith("video/")) {
            return "video";
        } else {
            return "auto"; // Let Cloudinary auto-detect
        }
    }

}
