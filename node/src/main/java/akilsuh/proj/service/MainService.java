package akilsuh.proj.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processTextMessage(Update update);
    void processCallbackMessage(Update update);
}
