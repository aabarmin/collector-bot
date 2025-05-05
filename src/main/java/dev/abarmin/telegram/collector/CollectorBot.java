package dev.abarmin.telegram.collector;

import dev.abarmin.telegram.collector.handler.command.StateHandler;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.handler.registry.CallbackHandlerRegistry;
import dev.abarmin.telegram.collector.handler.registry.CommandHandlerRegistry;
import dev.abarmin.telegram.collector.handler.registry.StateHandlerRegistry;
import dev.abarmin.telegram.collector.service.ChatState;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;

import static dev.abarmin.telegram.collector.handler.command.StartCommandHandler.START_COMMAND;

@Component
@RequiredArgsConstructor
public class CollectorBot implements LongPollingSingleThreadUpdateConsumer {

    private final CommandHandlerRegistry commandRegistry;
    private final CallbackHandlerRegistry callbackRegistry;
    private final StateHandlerRegistry stateRegistry;
    private final TelegramClient telegramClient;
    private final UserService userService;

    @Override
    public void consume(Update update) {
        if (hasText(update) || hasPhoto(update)) {
            final ChatState state = userService.getState(update);
            final Optional<StateHandler> stateHandler = stateRegistry.getHandler(state);
            if (stateHandler.isPresent()) {
                stateHandler.get().handle(update);
                return;
            }

            commandRegistry.getHandler(getText(update))
                    .ifPresentOrElse(handler -> handler.handle(update),
                            () -> noHandler(update));
        } else if (update.hasCallbackQuery()) {
            String data = UpdateHelper.getCallbackData(update);
            data = StringUtils.substringBefore(data, ":");
            callbackRegistry.getHandler(data)
                    .ifPresentOrElse(handler -> handler.handle(update),
                            () -> noHandler(update));
        }
    }

    private String getText(Update update) {
        final String text = update.getMessage().getText().trim();
        if (StringUtils.startsWith(text, START_COMMAND)) {
            return START_COMMAND;
        }
        return text;
    }

    private boolean hasText(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    private boolean hasPhoto(Update update) {
        return update.hasMessage() && update.getMessage().hasPhoto();
    }

    @SneakyThrows
    private void noHandler(Update update) {
        final long chatId;
        final String text;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            text = update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            text = UpdateHelper.getCallbackData(update);
        } else {
            throw new IllegalStateException("No message or callback query");
        }

        telegramClient.execute(SendMessage.builder()
                .chatId(chatId)
                .text("Нет хандлера чтобы обработать " + text)
                .build());
    }
}
