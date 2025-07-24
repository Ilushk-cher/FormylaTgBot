package akilsuh.proj;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Getter
@Setter
@Builder
public class Message {
    private Long chatId;
    private String text;
    private InlineKeyboardMarkup inlineKeyboardMarkup;
    private boolean isEdit;
    private int messageId;
    private boolean onlyReplyMarkupEdit;
    private String callBackId;

    public SendMessage getSendMessage() {
        return SendMessage.builder()
                .text(text)
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(chatId)
                .build();
    }

    public EditMessageText getEditMessageText() {
        return EditMessageText.builder()
                .text(text)
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }

    public EditMessageReplyMarkup getEditMessageReplyMarkup(InlineKeyboardMarkup inlineKeyboardMarkup) {
        return EditMessageReplyMarkup.builder()
                .replyMarkup(inlineKeyboardMarkup)
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }
}
