package com.telegram.bot.controller;

import com.telegram.bot.dao.MessageDao;
import com.telegram.bot.dto.AddMessage;
import com.telegram.bot.dto.Button;
import com.telegram.bot.dto.TelegramMessageButtonDto;
import com.telegram.bot.service.UploadFilesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class HomeController {
    public MessageDao messageDao = new MessageDao();
    private UploadFilesService uploadFilesService = new UploadFilesService();

    @GetMapping("/")
    public String home(Model model) {
        List<TelegramMessageButtonDto> messages = messageDao.readMessagesFromFile();
        model.addAttribute("messages", messages);
        return "home";
    }

    @GetMapping("/messages")
    public String getMessages(Model model) {
        List<TelegramMessageButtonDto> messages = messageDao.readMessagesFromFile();
        model.addAttribute("messages", messages);
        return "messages";
    }

    @GetMapping("/addMessage")
    public String showAddMessageForm(Model model) {
        return "add-message";
    }

    @PostMapping("/addMessage")
    public String addMessage(AddMessage message, @RequestParam("buttonText") Optional<List<String>> buttonTexts, @RequestParam("url") Optional<List<String>> urls) {
        TelegramMessageButtonDto telegramMessageButtonDto = new TelegramMessageButtonDto();
        telegramMessageButtonDto.setId(UUID.randomUUID().toString());
        telegramMessageButtonDto.setText(message.getText());
        if (!message.getPhoto().isEmpty()){
            String photoName = UUID.randomUUID() + "-" + message.getPhoto().getOriginalFilename();
            telegramMessageButtonDto.setPhotoName(photoName);
            uploadFilesService.uploadAndSaveImage(message.getPhoto(), photoName);
            telegramMessageButtonDto.setFileExtension(getFileExtension(message.getPhoto()));
        }
        if (buttonTexts.isPresent() && urls.isPresent()) {
            telegramMessageButtonDto.setButtons(createButtons(buttonTexts.get(), urls.get()));
        } else {
            telegramMessageButtonDto.setButtons(new ArrayList<>());
        }
        messageDao.saveMessageToFile(telegramMessageButtonDto);

        return "redirect:/messages";
    }

    @PostMapping("/deleteMessage")
    public String deleteMessage(TelegramMessageButtonDto message) {
        uploadFilesService.deleteImage(message);
        messageDao.deleteMessageFromFile(message);
        return "redirect:/messages";
    }

    public String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return null; // якщо розширення не знайдено або файл немає імені
    }

    private List<Button> createButtons(List<String> buttonTexts, List<String> urls) {
        List<Button> buttons = new ArrayList<>();

        int maxSize = Math.max(buttonTexts.size(), urls.size());

        for (int i = 0; i < maxSize; i++) {
            String buttonText = (i < buttonTexts.size()) ? buttonTexts.get(i) : null;
            String url = (i < urls.size()) ? urls.get(i) : null;

            Button button = new Button(buttonText, url);
            buttons.add(button);
        }

        return buttons;
    }
}
