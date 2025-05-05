package dev.abarmin.telegram.collector.handler.command;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;

public interface CommandHandler {

    Collection<String> commands();

    void handle(Update update);
}
