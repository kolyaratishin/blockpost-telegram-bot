package com.telegram.bot.service;

import com.telegram.bot.config.BotConfig;
import com.telegram.bot.dao.MessageDao;
import com.telegram.bot.dto.Button;
import com.telegram.bot.dto.TelegramMessageButtonDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Setter
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private MessageDao messageDao = new MessageDao();


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasChatJoinRequest()) {
            startCommandReceived(update.getChatJoinRequest().getUserChatId(), update.getChatJoinRequest().getUser().getFirstName());
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            String name = update.getMessage().getChat().getFirstName();

            switch (text) {
                case "/start":
                    startCommandReceived(chatId, name);
                    break;
                default:
                    sendMessage(createSendMessage(chatId, "Cannot recognize this command"));
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        System.out.println("Replied to " + name);
        List<TelegramMessageButtonDto> messages = messageDao.readMessagesFromFile();
        messages.forEach(message -> {
            if (message.getPhotoName() != null) {
                sendMessageWithPhotoOrVideo(chatId, message);
            } else {
                sendMessageWithLinkButton(chatId, message);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void sendMessageWithLinkButton(Long chatId, TelegramMessageButtonDto messageButton) {
        SendMessage sendMessage = createSendMessage(chatId, messageButton.getText());
        sendMessage.enableHtml(true);
        sendMessage.setReplyMarkup(getInlineKeyboardMarkup(messageButton));
        sendMessage(sendMessage);
    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Error occurred" + e.getMessage());
        }
    }

    private void sendMessageWithPhotoOrVideo(Long chatId, TelegramMessageButtonDto message) {
        if (message.isImage()) {
            File photo = UploadFilesService.getFileFromUploadDirectory(message.getPhotoName());
            SendPhoto sendPhoto = new SendPhoto();
            InputFile inputFile = new InputFile();
            inputFile.setMedia(photo);
            sendPhoto.setPhoto(inputFile);
            sendPhoto.setChatId(chatId);
            sendPhoto.setCaption(message.getText());
            sendPhoto.setReplyMarkup(getInlineKeyboardMarkup(message));
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                System.out.println("Error occurred" + e.getMessage());
            }
        } else {
            File video = UploadFilesService.getFileFromUploadDirectory(message.getPhotoName());
            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(chatId);
            sendVideo.setCaption(message.getText());
            InputFile inputFile = new InputFile();
            inputFile.setMedia(video);
            sendVideo.setVideo(inputFile);
            sendVideo.setReplyMarkup(getInlineKeyboardMarkup(message));
            try {
                execute(sendVideo);
            } catch (TelegramApiException e) {
                System.out.println("Error occurred" + e.getMessage());
            }
        }

    }

    private SendMessage createSendMessage(Long chatId, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        return sendMessage;
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(TelegramMessageButtonDto message) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = message.getButtons().stream()
                .map(this::createButton)
                .toList();
        keyboard.add(row);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private InlineKeyboardButton createButton(Button button) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(button.getButtonText());
        inlineKeyboardButton.setUrl(button.getUrl());
        return inlineKeyboardButton;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }
}
