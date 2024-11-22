package com.v01.techgear_server.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.dto.ImageDTO;
import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.model.Media;

public interface FileStorageService {
    CompletableFuture<ImageDTO> uploadSingleImage(MultipartFile file, UserDTO userDTO) throws IOException;
    CompletableFuture<List<ImageDTO>> uploadMultipleImage(List<MultipartFile> files, UserDTO userDTO) throws IOException;

    CompletableFuture<ImageDTO> updateUserImage(Long userId, MultipartFile newImageFile, UserDTO userDTO) throws IOException;

    Media uploadMedia(MultipartFile mediaFile) throws IOException;
    List<Media> uploadMultipleMedia(List<MultipartFile> mediaFile) throws IOException;


}