package com.v01.techgear_server.common.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.common.dto.ImageDTO;

import com.v01.techgear_server.common.dto.MediaDTO;

public interface FileStorageService {
    CompletableFuture<ImageDTO> storeSingleImage(MultipartFile file) throws IOException;
    CompletableFuture<List<ImageDTO>> storedMultipleImage(List<MultipartFile> files) throws IOException;

    MediaDTO storeMedia(MultipartFile mediaFile) throws IOException;

    List<MediaDTO> storeMultipleMedia(List<MultipartFile> mediaFile) throws IOException;

    CompletableFuture<Void> deleteImage(String publicId) throws IOException;
    CompletableFuture<Void> deleteMultipleImages(List<String> publicIds);
    CompletableFuture<Void> deleteMedia(String publicId);
    CompletableFuture<Void> deleteMultipleMedia(List<String> publicIds);
}