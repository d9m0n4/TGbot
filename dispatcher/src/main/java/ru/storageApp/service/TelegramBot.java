package ru.storageApp.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.storageApp.config.BotConfig;
import ru.storageApp.controller.UpdateController;


@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;

    private UpdateController updateController;


    public TelegramBot(BotConfig botConfig, UpdateController updateController) {
        this.config = botConfig;
        this.updateController = updateController;
    }

    @PostConstruct
    public void init() {
        updateController.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();

            System.out.println(message);
        }
    }


    public void sendAnswerMessage(SendMessage sendMessage) {
        System.out.println(sendMessage.getText());
    }
}
