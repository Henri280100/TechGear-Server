package com.v01.techgear_server.serviceImpls;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.service.FileStorageService;

@Service
public class CloudinaryFileStorageServiceImpl implements FileStorageService {

    @Autowired
    private Cloudinary cloudinary;
    private static final String CLOUDINARY_FOLDER = "techgear";


    @Override
    public Image storeImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", CLOUDINARY_FOLDER));

        String url = (String) uploadResult.get("url");
        String publicId = (String) uploadResult.get("public_id");
        String contentType = file.getContentType();

        Image image = new Image();
        image.setFilename(publicId);
        image.setContentType(contentType);
        image.setData(url.getBytes()); // Store URL as bytes if needed

        return image;
    }
    
}
