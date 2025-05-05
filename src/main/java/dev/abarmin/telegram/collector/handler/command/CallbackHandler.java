package dev.abarmin.telegram.collector.handler.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CallbackHandler {

    String callback();

    void handle(Update update);
}
