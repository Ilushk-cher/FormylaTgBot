package akilsuh.proj.service;

import akilsuh.proj.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface AnswerConsumer {
    void consume(Message message);
}
