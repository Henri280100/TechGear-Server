package com.v01.techgear_server.utils;

import com.v01.techgear_server.common.dto.ImageDTO;
import com.v01.techgear_server.common.service.FileStorageService;
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
import java.util.stream.Collectors;

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
    public <T> CompletableFuture<Object> saveImagesAndUpdateEntities(
            List<MultipartFile> images,
            List<T> entities,
            BiConsumer<T, String> setImageUrl,
            Consumer<List<T>> saveEntities
    ) throws IOException {
        if (images == null || entities == null || setImageUrl == null || saveEntities == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Inputs cannot be null"));
        }
        if (images.isEmpty() || entities.isEmpty()) {
            log.info("No images or entities provided; skipping processing");
            return CompletableFuture.completedFuture(null);
        }

        // Handle single or multiple images
        CompletableFuture<List<ImageDTO>> imageUploadFuture = images.size() == 1
                ? fileStorageService.storeSingleImage(images.getFirst()).thenApply(Collections::singletonList)
                : fileStorageService.storedMultipleImage(images);

        return imageUploadFuture.thenApply(imageDTOS -> {
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
    public <T> void saveVideoAndUpdateEntity(
            MultipartFile video,
            T entity,
            Consumer<T> setVideoUrl,
            Consumer<T> saveEntity
    ) throws IOException {
        fileStorageService.storeMedia(video)
                .thenAccept(videoDTO -> {
                    setVideoUrl.accept(entity);
                    saveEntity.accept(entity);
                    log.info("Saved entity with video");
                })
                .exceptionally(throwable -> {
                    log.error("Video upload or entity update failed", throwable);
                    return null;
                });
    }

    @Async
    @Transactional(rollbackFor = {IOException.class, DataAccessException.class})
    public <T> void saveMultipleVideoAndUpdateEntity(
            List<MultipartFile> videos,
            List<T> entities,
            BiConsumer<T, String> setVideoUrl,
            Consumer<List<T>> saveEntity
    ) throws IOException {
        fileStorageService.storeMultipleMedia(videos)
                .thenAccept(videoDTOs -> {
                    for (int i = 0; i < entities.size(); i++) {
                        if (i < videoDTOs.size()) {
                            setVideoUrl.accept(entities.get(i), videoDTOs.get(i).getMediaUrl());
                        }
                    }
                    saveEntity.accept(entities);
                    log.info("Saved {} entities with videos", entities.size());
                })
                .exceptionally(throwable -> {
                    log.error("Video upload or entity update failed", throwable);
                    return null;
                });
    }
}
