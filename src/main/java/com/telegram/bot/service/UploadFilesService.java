package com.telegram.bot.service;

import com.telegram.bot.dto.TelegramMessageButtonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class UploadFilesService {
    private static final String uploadDir = "photos/";

    public void uploadAndSaveImage(MultipartFile file, String photoName) {
        try {
            Path filePath = Paths.get(uploadDir + photoName);
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteImage(TelegramMessageButtonDto message) {
        try {
            Path path = Paths.get(uploadDir + message.getPhotoName());
            Files.delete(path);
            System.out.println("Файл успішно видалено");
        } catch (IOException e) {
            System.err.println("Помилка видалення файлу: " + e.getMessage());
        }
    }

    public static File getFileFromUploadDirectory(String filename) {
        return new File(uploadDir + filename);
    }
}
