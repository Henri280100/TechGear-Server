package com.v01.techgear_server.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;


import com.v01.techgear_server.model.Image;

public interface FileStorageService {
    Image storeImage(MultipartFile file) throws IOException;
}