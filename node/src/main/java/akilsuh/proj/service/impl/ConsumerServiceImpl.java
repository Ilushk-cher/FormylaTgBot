package akilsuh.proj.service.impl;

import akilsuh.proj.service.ConsumerService;
import akilsuh.proj.service.MainService;
import akilsuh.proj.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.chainsaw.Main;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static akilsuh.proj.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener (queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdate(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessage(update);

    }

    @Override
    @RabbitListener (queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessageUpdate(Update update) {
        log.debug("NODE: Doc message is received");

    }

    @Override
    @RabbitListener (queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessageUpdate(Update update) {
        log.debug("NODE: Photo message is received");

    }

    @Override
    @RabbitListener (queues = CALLBACK_MESSAGE_UPDATE)
    public void consumeCallbackMessageUpdate(Update update) {
        mainService.processCallbackMessage(update);
    }
}
