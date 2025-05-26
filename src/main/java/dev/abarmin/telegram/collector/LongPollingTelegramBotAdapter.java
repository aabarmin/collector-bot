package dev.abarmin.telegram.collector;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class LongPollingTelegramBotAdapter implements LongPollingSingleThreadUpdateConsumer {

    private final Consumer<Update> delegate;

    @Override
    public void consume(Update update) {
        delegate.accept(update);
    }
}
