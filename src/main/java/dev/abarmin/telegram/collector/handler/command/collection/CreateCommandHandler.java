package dev.abarmin.telegram.collector.handler.command.collection;

import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.UserEntity;
import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import dev.abarmin.telegram.collector.handler.command.StateHandler;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.service.ChatState;
import dev.abarmin.telegram.collector.repository.CollectionRepository;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collection;
import java.util.List;

import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;

@Component
@RequiredArgsConstructor
public class CreateCommandHandler implements CommandHandler, StateHandler {

    public static final String CREATE_COLLECTION_COMMAND = "➕ Создать коллекцию";

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final CollectionRepository collectionRepository;
    private final ListCommandHandler listCollections;

    @Override
    public Collection<String> commands() {
        return List.of(CREATE_COLLECTION_COMMAND);
    }

    @Override
    public Collection<ChatState> states() {
        return List.of(ChatState.COLLECTION_CREATION);
    }

    @Override
    public void handle(Update update) {
        final ChatState state = userService.getState(update);
        if (state == ChatState.COLLECTION_CREATION) {
            handleCreateCollection(update);
        } else {
            handleCreateStart(update);
        }
    }

    private void handleCreateStart(Update update) {
        userService.setState(update, ChatState.COLLECTION_CREATION);

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Введите название коллекции")
                .replyMarkup(ReplyKeyboardRemove.builder().build())
                .build()));
    }

    private void handleCreateCollection(Update update) {
        // todo, implement some validation like the name should be unique
        final String collectionName = update.getMessage().getText();
        if (StringUtils.isEmpty(collectionName)) {
            // todo, add some error message
            return;
        }

        final UserEntity user = userService.getUser(update);
        final CollectionEntity collection = CollectionEntity.builder()
                .name(collectionName)
                .userId(user.getId())
                .build();

        collectionRepository.save(collection);
        userService.setState(update, ChatState.STARTED);

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Новая коллекция успешно создана: " + collectionName)
                .build()));

        listCollections.handle(update);
    }
}
