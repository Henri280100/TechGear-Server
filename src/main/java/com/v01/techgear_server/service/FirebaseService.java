package com.v01.techgear_server.service;

import java.util.concurrent.CompletableFuture;
public interface FirebaseService {
    <T> CompletableFuture<Void> storeData(String path, String key, T data);
    CompletableFuture<Void> deleteData(String path, String key);
}
