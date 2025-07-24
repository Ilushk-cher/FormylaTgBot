package akilsuh.proj.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Log4j
public class BotInitializer {
    @Autowired
    TelegramBot bot;
    @Autowired
    UpdateController updateController;

    @PostConstruct
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(bot);
            updateController.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("Register bot error: " + e.getMessage());
        }
    }
}
