package com.telegram.bot.controller;

import com.telegram.bot.dto.TelegramMessageButtonDto;
import com.telegram.bot.service.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bot")
public class BotController {

    private final TelegramBot telegramBot;

    @PostMapping("/button/link")
    public String changeButtonLinks(@RequestBody List<TelegramMessageButtonDto> links){
        telegramBot.setMessageButtons(links);
        return "Твої повідомлення були зміненні";
    }
}
