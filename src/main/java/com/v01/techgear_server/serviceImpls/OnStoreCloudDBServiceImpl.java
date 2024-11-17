package com.v01.techgear_server.serviceImpls;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.v01.techgear_server.service.FirebaseService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OnStoreCloudDBServiceImpl implements FirebaseService {
    private final FirebaseDatabase firebaseDatabase;

    public OnStoreCloudDBServiceImpl(FirebaseApp firebaseApp) {
        this.firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
    }

    @Override
    public <T> CompletableFuture<Void> storeData(String path, String key, T data) {
        DatabaseReference reference = firebaseDatabase.getReference(path).child(key);
        return CompletableFuture.runAsync(() -> {
            ApiFuture<Void> future = reference.setValueAsync(data);
            try {
                future.get(); // Wait for the operation to complete
                log.info("Stored data at path: {}/{}", path, key);
            } catch (Exception e) {
                log.error("Error storing data at path: {}/{}", path, key, e);
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteData(String path, String key) {
        DatabaseReference reference = firebaseDatabase.getReference(path).child(key);
        return CompletableFuture.runAsync(() -> {
            ApiFuture<Void> future = reference.removeValueAsync();
            try {
                future.get(); // Wait for the operation to complete
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

}