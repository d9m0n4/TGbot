package ru.storageApp.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.storageApp.model.RabbitQueue;
import ru.storageApp.service.TelegramBot;
import ru.storageApp.service.UpdateProducer;
import ru.storageApp.utils.MessageUtils;

import static ru.storageApp.model.RabbitQueue.*;

@Component
@Log4j2
public class UpdateController {
    private TelegramBot telegramBot;
    private MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Update is null");
            return;
        }

        if (update.getMessage() != null) {
            distributeMessagesByType(update);
        } else {
            log.error("Update is null" + update);
        }
    }

    public void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.getText() != null) {
            processTextMessage(update);
        } else if (message.getDocument() != null) {
            processDocMessage(update);
        } else if (message.getPhoto() != null) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageType(update);
        }
    }

    public void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE ,update);
    }

    public void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE ,update);

    }

    public void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE ,update);

    }

    public void setUnsupportedMessageType(Update update) {
        var sendMessage = messageUtils.generateSendMessage(update, "Не верный тип сообщения");
        setView(sendMessage);
    }

    private void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
}
