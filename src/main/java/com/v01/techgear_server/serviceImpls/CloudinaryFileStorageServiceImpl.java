package com.v01.techgear_server.serviceimpls;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.v01.techgear_server.constant.ErrorMessageConstants;
import com.v01.techgear_server.dto.ImageDTO;
import com.v01.techgear_server.dto.MediaDTO;
import com.v01.techgear_server.enums.ImageTypes;
import com.v01.techgear_server.exception.FileUploadingException;
import com.v01.techgear_server.mapping.ImageMapper;
import com.v01.techgear_server.mapping.MediaMapper;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.Media;
import com.v01.techgear_server.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryFileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;
    private final ImageMapper imageMapper;
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
    public CompletableFuture<Void> deleteImage(String publicId) throws IOException {
        return CompletableFuture.runAsync(() -> {
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
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
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
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
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
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
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                }
            } catch (Exception e) {
                log.error(ErrorMessageConstants.ERROR_DELETING_MULTI_MEDIA, e);
                throw new FileUploadingException(ErrorMessageConstants.ERROR_DELETING_MULTI_MEDIA, e);
            }
        });
    }

    @Override
    public CompletableFuture<ImageDTO> storeSingleImage(MultipartFile file)
            throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            try {

                validateFiles(file);

                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap(FOLDER, CLOUDINARY_FOLDER,
                                RESOURCE_TYPE, "image",
                                QUALITY, AUTO_QUALITY, // Auto quality based on eco mode
                                FETCH_FORMAT, "auto", // Automatically optimize the image format
                                WIDTH, 1024, // Resize image to have a max width of 1024px
                                CROP, LIMIT // Limit cropping to avoid exceeding width
                ));

                String url = (String) uploadResult.get(SECURE_URL);
                String publicId = (String) uploadResult.get(PUBLIC_ID);
                Integer width = (Integer) uploadResult.get(WIDTH);
                Integer height = (Integer) uploadResult.get(HEIGHT);

                Image image = new Image();
                image.setId(Long.parseLong(publicId));
                image.setImageUrl(url);
                image.setWidth(width);
                image.setHeight(height);

                image.setImageTypes(ImageTypes.GENERIC);
                return imageMapper.toDTO(image);
            } catch (Exception e) {
                throw new FileUploadingException("Unexpected error during image upload", e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletableFuture<List<ImageDTO>> storedMultipleImage(List<MultipartFile> files)
            throws IOException {
        return CompletableFuture.supplyAsync(() -> {

            List<ImageDTO> uploadedImages = new ArrayList<>();

            // Validate and filter non-empty files
            List<MultipartFile> validFiles = files.stream()
                    .filter(file -> !file.isEmpty())
                    .toList();

            if (validFiles.isEmpty()) {
                throw new IllegalArgumentException("No valid files to upload");
            }

            try {
                for (MultipartFile file : validFiles) {
                    validateFiles(file); // Validate image file

                    // Upload image to Cloudinary
                    Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    RESOURCE_TYPE, "image",
                                    QUALITY, AUTO_QUALITY, // Auto quality based on eco mode
                                    FETCH_FORMAT, "auto", // Automatically optimize the image format
                                    WIDTH, 1024, // Resize image to have a max width of 1024px
                                    CROP, LIMIT // Limit cropping to avoid exceeding width
                    ));

                    String url = (String) uploadResult.get(SECURE_URL);
                    String publicId = (String) uploadResult.get(PUBLIC_ID);
                    Integer width = (Integer) uploadResult.get(WIDTH);
                    Integer height = (Integer) uploadResult.get(HEIGHT);

                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setFileName(publicId);
                    imageDTO.setImageUrl(url); // Set the image URL

                    imageDTO.setDimensions(new ImageDTO.ImageDimensions(width, height));
                    imageDTO.setCreatedAt(LocalDateTime.now()); // Set creation timestamp

                    imageDTO.setType(ImageTypes.GENERIC);
                    // Add the uploaded image to the list
                    uploadedImages.add(imageDTO);
                }

            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to upload multiple files", e);
            }

            return uploadedImages;
        });

    }

    @Override
    public MediaDTO storeMedia(MultipartFile mediaFile) throws IOException {
        try {
            validateFiles(mediaFile);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(mediaFile.getBytes(),
                    ObjectUtils.asMap(
                            FOLDER, CLOUDINARY_FOLDER,
                            RESOURCE_TYPE, "video", // Auto-detect resource type (image, video, etc.)
                            QUALITY, AUTO_QUALITY, // Auto quality based on optimization
                            FETCH_FORMAT, "auto" // Automatically optimize the format

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
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            FOLDER, CLOUDINARY_FOLDER,
                            RESOURCE_TYPE, "auto", // Auto-detect resource type (image, video, etc.)
                            QUALITY, AUTO_QUALITY, // Auto quality based on optimization
                            FETCH_FORMAT, "auto", // Automatically optimize the format
                            PUBLIC_ID, file.getOriginalFilename() // Optional: Set public ID
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
