package com.v01.techgear_server.serviceImpls;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.v01.techgear_server.dto.ImageDTO;
import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.enums.ImageTypes;
import com.v01.techgear_server.exception.FileUploadingException;
import com.v01.techgear_server.exception.UserNotFoundException;
import com.v01.techgear_server.model.Media;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryFileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;
    private static final String CLOUDINARY_FOLDER = "techgear";
    private final UserRepository userRepository;
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
    public CompletableFuture<ImageDTO> updateUserImage(Long userId, MultipartFile newImageFile, UserDTO userDTO)
            throws IOException {
        return CompletableFuture.supplyAsync(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User  with ID " + userId + " not found"));

            try {
                // Upload the new image file
                ImageDTO newImageDTO = uploadSingleImage(newImageFile, userDTO).join(); // Using join to wait for the
                                                                                        // upload result

                // Update the user's avatar
                user.setUserAvatar(newImageDTO.toEntity());
                userRepository.save(user); // Save the updated user

                return newImageDTO; // Return the new ImageDTO
            } catch (IOException e) {
                throw new RuntimeException("Unexpected error while updating user's image");
            }
        });
    }

    @Override
    public CompletableFuture<ImageDTO> uploadSingleImage(MultipartFile file, UserDTO userDTO) throws IOException {
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

                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setFileName(publicId);
                imageDTO.setImageUrl(url); // Set the image URL
                imageDTO.setDimensions(new ImageDTO.ImageDimensions(width, height));
                imageDTO.setCreatedAt(LocalDateTime.now()); // Set creation timestamp

                imageDTO.setImageType(ImageTypes.GENERIC);

                return imageDTO;
            } catch (Exception e) {
                throw new FileUploadingException("Unexpected error during image upload", e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletableFuture<List<ImageDTO>> uploadMultipleImage(List<MultipartFile> files, UserDTO userDTO)
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
                    
                    imageDTO.setImageType(ImageTypes.GENERIC);
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
    public Media uploadMedia(MultipartFile mediaFile) throws IOException {
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

            return media;
        } catch (IOException e) {
            throw new IOException("Failed to upload media due to I/O error", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to upload media", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Media> uploadMultipleMedia(List<MultipartFile> mediaFile) throws IOException {
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
        return uploadedMedia;
    }

    private void validateFiles(MultipartFile file) throws FileUploadingException {
        if (file == null || file.isEmpty()) {
            throw new FileUploadingException("Cannot upload an empty file");
        }
    }

}
