package akilsuh.proj.controller;

import akilsuh.proj.Message;
import akilsuh.proj.RabbitQueue;
import akilsuh.proj.service.UpdateProducer;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import static akilsuh.proj.RabbitQueue.*;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final UpdateProducer updateProducer;

    public UpdateController(UpdateProducer updateProducer) {
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage() || update.hasCallbackQuery()) {
            distributeMessagesByType(update);
        } else {
            log.error("Received unsupported message type: " + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        if (update.hasCallbackQuery()) {
            processCallbackMessage(update);
            return;
        }
        var message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        } else if (message.hasDocument()) {
            processDocMessage(update);
        } else if (message.hasPhoto()) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        Message message = Message.builder().build();
        message.setText("Unsupported message type");
        message.setChatId(update.getMessage().getChatId());
        setView(message);
    }

    public void setView(Message message) {
        telegramBot.sendAnsweringMessage(message);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void processDocMessage(Update update) {
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
    }

    private void processCallbackMessage(Update update) {
        updateProducer.produce(CALLBACK_MESSAGE_UPDATE, update);
    }
}
