package com.v01.techgear_server.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.Media;
import com.v01.techgear_server.service.FileStorageService;

@RestController
@RequestMapping("/api/v01/upload")
public class UploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/single-file")
    public ResponseEntity<Image> uploadSingleImage(@RequestParam("file") MultipartFile file) {
        try {
            // Call the service to upload the file
            Image uploadedImage = fileStorageService.uploadSingleImage(file);
            return ResponseEntity.ok(uploadedImage);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/multiple-files")
    public ResponseEntity<List<Image>> uploadMultipleImages(@RequestParam("files") List<MultipartFile> files) {
        try {
            List<Image> uploadedImages = fileStorageService.uploadMultipleImage(files);
            return ResponseEntity.ok(uploadedImages);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null); // Handle error
        }
    }

    @PostMapping("/media-file")
    public ResponseEntity<Media> uploadMediaFile(@RequestParam("mediaFile") MultipartFile mediaFile)
            throws IOException {
        try {
            Media media = fileStorageService.uploadMedia(mediaFile);
            return ResponseEntity.ok(media);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/multiple-media-files")
    public ResponseEntity<?> uploadMultipleMediaFiles(
            @RequestPart("mediaFiles") List<MultipartFile> mediaFiles) {
        try {
            List<Media> uploadedMedia = fileStorageService.uploadMultipleMedia(mediaFiles);
            return ResponseEntity.ok(uploadedMedia);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload multiple media"); // Handle
                                                                                                                    // error
        }
    }
}
