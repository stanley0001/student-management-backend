package com.compulynx.student_management.files.services.implementation;

/**
 * @author Stanley Mungai
 */

import com.compulynx.student_management.files.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService implements FileService {
    @Value("${spring.file.path}")
    private static final String basePath = "";

    public String saveFile(MultipartFile file, String subFolder, String filename) throws IOException {
        Path folderPath = Paths.get(basePath, subFolder);

        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }

        Path filePath = folderPath.resolve(filename);
        file.transferTo(filePath.toFile());

        return filePath.toString();
    }

    public byte[] readFile(String subFolder, String filename) throws IOException {
        Path filePath = Paths.get(basePath, subFolder, filename);
        return Files.readAllBytes(filePath);
    }

    public boolean deleteFile(String subFolder, String filename) {
        Path filePath = Paths.get(basePath, subFolder, filename);
        File file = filePath.toFile();
        return file.exists() && file.delete();
    }
}
