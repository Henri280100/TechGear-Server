package com.v01.techgear_server.utils;

import com.v01.techgear_server.shared.dto.ImageDTO;
import com.v01.techgear_server.shared.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final PlatformTransactionManager transactionManager;
    private final FileStorageService fileStorageService;

    /**
     * Generic image upload and entity update handler
     *
     * @param images       list of image files
     * @param entities     list of entities (can be Product, User, Post, etc.)
     * @param setImageUrl  how to set image URL for each entity
     * @param saveEntities logic to save all updated entities
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public <T> void saveImagesAndUpdateEntities(
            List<MultipartFile> images,
            List<T> entities,
            BiConsumer<T, String> setImageUrl,
            Consumer<List<T>> saveEntities
    ) throws IOException {
        if (images == null || entities == null || setImageUrl == null || saveEntities == null) {
            CompletableFuture.failedFuture(new IllegalArgumentException("Inputs cannot be null"));
            return;
        }
        if (images.isEmpty() || entities.isEmpty()) {
            log.info("No images or entities provided; skipping processing");
            CompletableFuture.completedFuture(null);
            return;
        }

        // Handle single or multiple images
        CompletableFuture<List<ImageDTO>> imageUploadFuture = images.size() == 1
                ? fileStorageService.storeSingleImage(images.getFirst()).thenApply(Collections::singletonList)
                : fileStorageService.storedMultipleImage(images);

        imageUploadFuture.thenApply(imageDTOS -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            return transactionTemplate.execute(status -> {
                try {
                    for (int i = 0; i < entities.size() && i < imageDTOS.size(); i++) {
                        setImageUrl.accept(entities.get(i), imageDTOS.get(i).getImageUrl());
                    }
                    saveEntities.accept(entities);
                    log.info("Saved {} entities with {} images", entities.size(), imageDTOS.size());
                    return null;
                } catch (Exception e) {
                    log.error("Failed to update or save entities", e);
                    throw new RuntimeException("Entity update/save failed", e);
                }
            });
        }).exceptionally(throwable -> {
            log.error("Image upload failed", throwable);
            throw new RuntimeException("Image upload failed", throwable);
        });
    }


    @Async
    @Transactional(rollbackFor = {IOException.class, DataAccessException.class})
    public <T> void saveVideosAndUpdateEntity(
            List<MultipartFile> videos,
            List<T> entities,
            BiConsumer<T, String> setVideoUrl,
            Consumer<List<T>> saveEntities
    ) throws IOException {
        if (videos == null || entities == null || setVideoUrl == null || saveEntities == null) {
            CompletableFuture.failedFuture(new IllegalArgumentException("Inputs cannot be null"));
            return;
        }
        if (videos.isEmpty() || entities.isEmpty()) {
            log.info("No videos or entities provided; skipping processing");
            CompletableFuture.completedFuture(null);
            return;
        }
        // Handle single or multiple videos
        CompletableFuture<List<ImageDTO>> videoUploadFuture = videos.size() == 1
                ? fileStorageService.storeSingleImage(videos.getFirst()).thenApply(Collections::singletonList)
                : fileStorageService.storedMultipleImage(videos);
        videoUploadFuture.thenApply(videoDTOS -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            return transactionTemplate.execute(status -> {
                try {
                    for (int i = 0; i < entities.size() && i < videoDTOS.size(); i++) {
                        setVideoUrl.accept(entities.get(i), videoDTOS.get(i).getImageUrl());
                    }
                    saveEntities.accept(entities);
                    log.info("Saved {} entities with {} videos", entities.size(), videoDTOS.size());
                    return null;
                } catch (Exception e) {
                    log.error("Failed to update or save entities", e);
                    throw new RuntimeException("Entity update/save failed", e);
                }
            });
        }).exceptionally(throwable -> {
            log.error("Video upload failed", throwable);
            throw new RuntimeException("Video upload failed", throwable);
        });
    }
}



