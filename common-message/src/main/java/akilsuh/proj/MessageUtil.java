package akilsuh.proj;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class MessageUtil {
    public static int getMessageId(CallbackQuery callbackQuery) {
        String messageToString = callbackQuery.getMessage().toString();
        int startIndex = messageToString.indexOf("messageId=");
        int endIndex = messageToString.indexOf(",", startIndex);

        try {
            return Integer.parseInt(messageToString.substring(startIndex + "messageId=".length(), endIndex));
        } catch (NumberFormatException e) {

        }
        return 0;
    }
}
