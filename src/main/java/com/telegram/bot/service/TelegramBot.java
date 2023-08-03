package com.telegram.bot.service;

import com.telegram.bot.config.BotConfig;
import com.telegram.bot.dto.TelegramMessageButtonDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Setter
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private List<TelegramMessageButtonDto> messageButtons = List.of(
            new TelegramMessageButtonDto("Текст 1", "https://t.me/1", "Перейти за посиланням"),
            new TelegramMessageButtonDto("Текст 2", "https://t.me/2", "Перейти за посиланням"),
            new TelegramMessageButtonDto("Текст 3", "https://t.me/3", "Перейти за посиланням"));

    @Override
    public void onUpdateReceived(Update update) {
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
        messageButtons.forEach(messageButton -> {
            sendMessageWithLinkButton(chatId, messageButton);
            try {
                Thread.sleep(5000);
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

    private SendMessage createSendMessage(Long chatId, String answer) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        return sendMessage;
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(TelegramMessageButtonDto messageButton) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton(messageButton));
        keyboard.add(row);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private InlineKeyboardButton createButton(TelegramMessageButtonDto messageButton) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(messageButton.getButtonText());
        button.setUrl(messageButton.getUrl());
        return button;
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
