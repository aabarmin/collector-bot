package dev.abarmin.telegram.collector;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class TestTelegramBotAdapter {

    private final Consumer<Update> delegate;

    public void onUpdate(final Update update) {
        delegate.accept(update);
    }

}
