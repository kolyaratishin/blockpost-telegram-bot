package com.telegram.bot.dao;

import com.telegram.bot.dto.TelegramMessageButtonDto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MessageDao {
    private final String filePath = "messages.txt";

    public MessageDao() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Помилка при створенні файлу: " + e.getMessage());
            }
        }
    }

    public List<TelegramMessageButtonDto> readMessagesFromFile() {
        List<TelegramMessageButtonDto> messages = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {

            messages = (List<TelegramMessageButtonDto>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Файл не знайдено: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Помилка вводу-виводу: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Клас не знайдено: " + e.getMessage());
        }
        return messages;
    }

    public void saveMessageToFile(TelegramMessageButtonDto message) {
        List<TelegramMessageButtonDto> messages = readMessagesFromFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            messages.add(message);
            oos.writeObject(messages);
            System.out.println("Повідомлення збережено у файл " + filePath);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не знайдено: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Помилка вводу-виводу: " + e.getMessage());
        }
    }

    public void deleteMessageFromFile(TelegramMessageButtonDto message) {
        List<TelegramMessageButtonDto> messages = readMessagesFromFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(messages.stream().filter(obj -> !obj.getId().equals(message.getId())).collect(Collectors.toList()));
            System.out.println("Повідомлення збережено у файл " + filePath);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не знайдено: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Помилка вводу-виводу: " + e.getMessage());
        }
    }
}
