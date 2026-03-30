package com.tinhnguyenxanh.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public String uploadFile(MultipartFile file, String subFolder) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path folder = Paths.get(uploadDir, subFolder);
        Files.createDirectories(folder);
        Path targetPath = folder.resolve(fileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + subFolder + "/" + fileName;
    }

    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path path = Paths.get(uploadDir + relativePath.replace("/uploads", ""));
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("[FILE DELETE ERROR] " + e.getMessage());
        }
    }
}
