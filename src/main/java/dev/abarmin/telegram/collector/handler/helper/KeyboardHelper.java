package dev.abarmin.telegram.collector.handler.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeyboardHelper {

    public static InlineKeyboardRow row(Consumer<InlineKeyboardRow> consumer) {
        final InlineKeyboardRow row = new InlineKeyboardRow();
        consumer.accept(row);
        return row;
    }

    public static KeyboardRow keyboardRow(String... messages) {
        final KeyboardRow row = new KeyboardRow();
        for (String message : messages) {
            row.add(message);
        }
        return row;
    }

}
