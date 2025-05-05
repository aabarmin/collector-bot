package dev.abarmin.telegram.collector.handler.command.collection;

import dev.abarmin.telegram.collector.domain.CollectionAccessEntity;
import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.UserEntity;
import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import dev.abarmin.telegram.collector.handler.command.context.CurrentItemContext;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.repository.CollectionAccessRepository;
import dev.abarmin.telegram.collector.repository.CollectionRepository;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collection;
import java.util.List;

import static dev.abarmin.telegram.collector.handler.command.collection.ListCommandHandler.COLLECTIONS_LIST;
import static dev.abarmin.telegram.collector.handler.command.collection.ShareCollectionCommandHandler.SHARE_COLLECTION;
import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;
import static dev.abarmin.telegram.collector.handler.helper.KeyboardHelper.keyboardRow;

@Component
@RequiredArgsConstructor
public class AccessListCommandHandler implements CommandHandler {

    public static final String MANAGE_COLLECTION_ACCESS = "⤴\uFE0F Поделиться";

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final CollectionRepository collectionRepository;
    private final CollectionAccessRepository accessRepository;

    @Override
    public Collection<String> commands() {
        return List.of(MANAGE_COLLECTION_ACCESS);
    }

    @Override
    public void handle(Update update) {
        final CollectionEntity collection = getCollection(update);
        final List<CollectionAccessEntity> access = accessRepository.findByCollectionId(collection.getId());

        if (access.isEmpty()) {
            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text("К этой коллекции есть доступ только у вас")
                    .replyMarkup(ReplyKeyboardMarkup.builder()
                            .resizeKeyboard(true)
                            .keyboardRow(keyboardRow(SHARE_COLLECTION))
                            .keyboardRow(keyboardRow(COLLECTIONS_LIST))
                            .build())
                    .build()));
        } else {
            final StringBuilder builder = new StringBuilder("К этой коллекции есть доступ у:\n");
            for (CollectionAccessEntity entity : access) {
                final UserEntity user = userService.getUser(entity.getUserId());
                builder.append("- ")
                        .append(user.getUsername())
                        .append(" (")
                        .append(entity.getPermissions().getValue())
                        .append(")\n");
            }
            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text(builder.toString())
                    .replyMarkup(ReplyKeyboardMarkup.builder()
                            .resizeKeyboard(true)
                            .keyboardRow(keyboardRow(SHARE_COLLECTION))
                            .keyboardRow(keyboardRow(COLLECTIONS_LIST))
                            .build())
                    .build()));
        }
    }

    private CollectionEntity getCollection(Update update) {
        final CurrentItemContext context = userService.getContext(update, CurrentItemContext.class);
        return collectionRepository.findById(context.getCollectionId())
                .orElseThrow(() -> new IllegalStateException("Collection not found"));
    }
}
