package com.telegram.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMessage {
    private String text;
    private MultipartFile photo;
    private List<String> buttonTexts;
    private List<String> urls;
}
