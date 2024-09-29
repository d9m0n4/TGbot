package ru.storageApp.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.storageApp.config.BotConfig;
import ru.storageApp.controller.UpdateController;


@Component
@Log4j2
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;

    private final UpdateController updateController;


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

        updateController.processUpdate(update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();

            System.out.println(message);
        }

    }


    public void sendAnswerMessage(SendMessage sendMessage) {
        if (sendMessage != null) {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
        System.out.println(sendMessage.getText());
    }
}
