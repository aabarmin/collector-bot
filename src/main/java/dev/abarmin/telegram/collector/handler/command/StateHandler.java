package dev.abarmin.telegram.collector.handler.command;

import dev.abarmin.telegram.collector.service.ChatState;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;

public interface StateHandler {

    Collection<ChatState> states();

    void handle(Update update);
}
