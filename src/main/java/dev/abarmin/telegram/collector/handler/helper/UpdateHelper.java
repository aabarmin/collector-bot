package dev.abarmin.telegram.collector.handler.helper;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;

@UtilityClass
public class UpdateHelper {

    public long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        throw new IllegalStateException("No chat id found in update");
    }

    public static String getText(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getText();
        }
        throw new IllegalStateException("No text found in update");
    }

    public static int getMessageId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getMessageId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getMessageId();
        }
        throw new IllegalStateException("No message id found in update");
    }

    public String getCallbackData(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }
        throw new IllegalStateException("No callback data found in update");
    }

}
