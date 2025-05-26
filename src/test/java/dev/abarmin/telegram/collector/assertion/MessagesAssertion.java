package dev.abarmin.telegram.collector.assertion;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collection;

@RequiredArgsConstructor
public class MessagesAssertion {

    private final Collection<SendMessage> messages;

    public static MessagesAssertion assertThat(Collection<SendMessage> messages) {
        return new MessagesAssertion(messages);
    }

    public MessagesAssertion hasMessageWithText(String text) {
        Assertions.assertThat(messages)
                .extracting(SendMessage::getText)
                .contains(text);

        return this;
    }

    public MessagesAssertion hasReplyKeyboardButton(String text) {
        boolean found = false;
        for (SendMessage message : messages) {
            if (message.getReplyMarkup() != null &&
                    message.getReplyMarkup() instanceof ReplyKeyboardMarkup keyboard) {

                for (KeyboardRow row : keyboard.getKeyboard()) {
                    for (KeyboardButton button : row) {
                        if (StringUtils.equalsIgnoreCase(button.getText(), text)) {
                            found = true;
                            break;
                        }
                    }
                }

            }
        }
        Assertions.assertThat(found)
                .withFailMessage("Expected to find reply keyboard button with text '%s', but none found", text)
                .isTrue();

        return this;
    }

}
