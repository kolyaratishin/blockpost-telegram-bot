package com.telegram.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramMessageButtonDto implements Serializable {
    private String id;
    private String text;
    private String photoName;
    private String fileExtension;
    private List<Button> buttons;

    public boolean isImage() {
        if (fileExtension != null) {
            return fileExtension.equals(".png") || fileExtension.equals(".jpg") || fileExtension.equals(".jpeg");
        }
        return false;
    }
}
