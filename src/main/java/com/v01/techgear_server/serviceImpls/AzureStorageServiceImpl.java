package com.v01.techgear_server.serviceImpls;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.v01.techgear_server.service.AzureStorageService;

@Service
public class AzureStorageServiceImpl implements AzureStorageService {

    private BlobServiceClient blobServiceClient;
    private BlobContainerClient blobContainerClient;

    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String blobContainerName;

    @Value("${spring.cloud.azure.storage.blob.account-key}")
    private String accountKey;

    public AzureStorageServiceImpl(@Value("${spring.cloud.azure.storage.blob.account-name}") String accountName) {
        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(String.format(
                        "DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
                        accountName, accountKey))
                .buildClient();
        this.blobContainerClient = blobServiceClient.getBlobContainerClient(blobContainerName);
    }

    @Override
    public String uploadEmailBody(String emailBody, String fileName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
        byte[] contentBytes = emailBody.getBytes();
        try (ByteArrayInputStream dataStream = new ByteArrayInputStream(contentBytes)) {
            blobClient.upload(dataStream, contentBytes.length, true);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while uploading email body to Azure Blob Storage", e);
        }
        return blobClient.getBlobUrl();
    }

}
