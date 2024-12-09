package com.umbrellanow.unow_backend.integrations.s3;

import java.io.InputStream;
import java.util.List;

public interface S3Service {
    void createBucketIfNotExists();
    void uploadFile(String path, String fileName, InputStream inputStream, long size, String contentType);
    InputStream getFile(String fileName);
    void deleteFile(String fileName);
    void deleteDirectory(String path);
    String createPath(String basePath, String objectId);
    List<String> listFiles(String path);
}
