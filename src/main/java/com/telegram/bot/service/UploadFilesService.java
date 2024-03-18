package com.telegram.bot.service;

import com.telegram.bot.dto.TelegramMessageButtonDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadFilesService {
    private static final String uploadDir = "/src/main/resources/static/photos/";

    public void uploadAndSaveImage(MultipartFile file, String photoName) {
        try {
            Path currentPath = Paths.get(".");
            Path absolutePath = currentPath.toAbsolutePath();
            Path filePath = Paths.get(absolutePath + uploadDir + photoName);
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteImage(TelegramMessageButtonDto message) {
        try {
            Path currentPath = Paths.get(".");
            Path absolutePath = currentPath.toAbsolutePath();
            Path path = Paths.get(absolutePath + uploadDir + message.getPhotoName());
            Files.delete(path);
            System.out.println("Файл успішно видалено");
        } catch (IOException e) {
            System.err.println("Помилка видалення файлу: " + e.getMessage());
        }
    }

    public static File getFileFromUploadDirectory(String filename) {
        Path currentPath = Paths.get(".");
        Path absolutePath = currentPath.toAbsolutePath();
        return new File(absolutePath + uploadDir + filename);
    }
}
