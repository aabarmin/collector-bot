package dev.abarmin.telegram.collector;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class TestTelegramUser {

    private final TestTelegramBotAdapter botAdapter;

    public void sendUpdate(Update update) {
        botAdapter.onUpdate(update);
    }

}
