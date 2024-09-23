package com.v01.techgear_server.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;


import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.Media;

public interface FileStorageService {
    Image uploadSingleImage(MultipartFile file) throws IOException;
    List<Image> uploadMultipleImage(List<MultipartFile> files) throws IOException;
    Media uploadMedia(MultipartFile mediaFile) throws IOException;
    List<Media> uploadMultipleMedia(List<MultipartFile> mediaFile) throws IOException;
}