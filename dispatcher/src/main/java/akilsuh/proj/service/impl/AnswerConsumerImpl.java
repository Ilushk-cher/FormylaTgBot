package akilsuh.proj.service.impl;

import akilsuh.proj.Message;
import akilsuh.proj.controller.UpdateController;
import akilsuh.proj.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static akilsuh.proj.RabbitQueue.ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener (queues = ANSWER_MESSAGE)
    public void consume(Message message) {
        updateController.setView(message);
    }
}
