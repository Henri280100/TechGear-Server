package com.v01.techgear_server.serviceImpls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.v01.techgear_server.exception.FileUploadingException;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.Media;
import com.v01.techgear_server.service.FileStorageService;

@Service
public class CloudinaryFileStorageServiceImpl implements FileStorageService {

    @Autowired
    private Cloudinary cloudinary;
    private static final String CLOUDINARY_FOLDER = "techgear";

    @Override
    public Image uploadSingleImage(MultipartFile file) throws IOException {
        try {
            validateFiles(file);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", CLOUDINARY_FOLDER,
                            "resource_type", "image",
                            "quality", "auto:good", // Auto quality based on eco mode
                            "fetch_format", "auto", // Automatically optimize the image format
                            "width", 1024, // Resize image to have a max width of 1024px
                            "crop", "limit" // Limit cropping to avoid exceeding width
                    ));

            String url = (String) uploadResult.get("url");
            String publicId = (String) uploadResult.get("public_id");
            String contentType = file.getContentType();

            Image image = new Image();
            image.setFilename(publicId);
            image.setContentType(contentType);
            image.setData(url.getBytes()); // Store URL as bytes if needed
            // getImage(url, publicId, contentType);

            return image;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to upload multiple files");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Image> uploadMultipleImage(List<MultipartFile> files) throws IOException {
        List<Image> uploadedImages = new ArrayList<>();

        // Validate and filter non-empty files
        List<MultipartFile> validFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .collect(Collectors.toList());

        if (validFiles.isEmpty()) {
            throw new IllegalArgumentException("No valid files to upload");
        }

        try {
            for (MultipartFile file : validFiles) {
                validateFiles(file); // Validate image file

                // Upload image to Cloudinary
                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", CLOUDINARY_FOLDER,
                                "resource_type", "image",
                                "quality", "auto:good", // Auto quality
                                "fetch_format", "auto", // Auto format
                                "width", 1024, // Resize width
                                "crop", "limit" // Crop limit
                        ));

                // Extract details from Cloudinary response
                String url = (String) uploadResult.get("secure_url"); // Use secure URL
                String publicId = (String) uploadResult.get("public_id");
                String contentType = file.getContentType();

                // Create Image object
                Image image = new Image();
                // getImage(url, publicId, contentType);
                image.setFilename(publicId);
                image.setContentType(contentType);
                image.setData(url.getBytes());
                // Add the uploaded image to the list
                uploadedImages.add(image);
            }

        } catch (IOException e) {
            throw new IOException("Failed to upload files due to I/O error", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to upload multiple files", e);
        }

        return uploadedImages;

    }

    @Override
    public Media uploadMedia(MultipartFile mediaFile) throws IOException {
        try {
            validateFiles(mediaFile);

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(mediaFile.getBytes(),
                    ObjectUtils.asMap(
                            "folder", CLOUDINARY_FOLDER,
                            "resource_type", "video", // Auto-detect resource type (image, video, etc.)
                            "quality", "auto:best", // Auto quality based on optimization
                            "fetch_format", "auto" // Automatically optimize the format
                    // "transformation", ObjectUtils.asMap(
                    // "quality", "auto", // Adaptive quality
                    // "fetch_format", "auto", // Adaptive format
                    // "video_codec", "auto", // Adaptive video codec
                    // "audio_codec", "best" // Adaptive audio codec
                    // )

                    ));

            String url = (String) uploadResult.get("secure_url"); // Use secure URL
            String publicId = (String) uploadResult.get("public_id");
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
                .collect(Collectors.toList());

        if (validFiles.isEmpty()) {
            throw new IllegalArgumentException("No valid files to upload");
        }

        for (MultipartFile file : validFiles) {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", CLOUDINARY_FOLDER,
                            "resource_type", "auto", // Auto-detect resource type (image, video, etc.)
                            "quality", "auto:good", // Auto quality based on optimization
                            "fetch_format", "auto", // Automatically optimize the format
                            // "transformation", ObjectUtils.asMap(
                            // "quality", "auto", // Adaptive quality
                            // "fetch_format", "auto", // Adaptive format
                            // "video_codec", "auto", // Adaptive video codec
                            // "audio_codec", "auto" // Adaptive audio codec
                            // ),
                            "public_id", file.getOriginalFilename() // Optional: Set public ID
                    ));

            String url = (String) uploadResult.get("secure_url"); // Use secure URL
            String publicId = (String) uploadResult.get("public_id");
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
