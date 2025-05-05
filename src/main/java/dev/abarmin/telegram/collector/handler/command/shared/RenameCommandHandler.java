package dev.abarmin.telegram.collector.handler.command.shared;

import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import dev.abarmin.telegram.collector.handler.command.StateHandler;
import dev.abarmin.telegram.collector.handler.command.context.CurrentItemContext;
import dev.abarmin.telegram.collector.handler.command.item.ListItemsCallbackHandler;
import dev.abarmin.telegram.collector.handler.command.item.ViewItemCallbackHandler;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.service.ChatState;
import dev.abarmin.telegram.collector.repository.CollectionItemsRepository;
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
import static dev.abarmin.telegram.collector.service.ChatState.COLLECTION_ITEM_RENAMING;
import static dev.abarmin.telegram.collector.service.ChatState.COLLECTION_RENAMING;

@Component
@RequiredArgsConstructor
public class RenameCommandHandler implements CommandHandler, StateHandler {

    public static final String RENAME_COMMAND = "\uD83D\uDCDD Переименовать";

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final CollectionItemsRepository itemsRepository;
    private final CollectionRepository collectionRepository;
    private final ListItemsCallbackHandler listHandler;
    private final ViewItemCallbackHandler itemHandler;

    @Override
    public Collection<String> commands() {
        return List.of(RENAME_COMMAND);
    }

    @Override
    public Collection<ChatState> states() {
        return List.of(COLLECTION_RENAMING, COLLECTION_ITEM_RENAMING);
    }

    @Override
    public void handle(Update update) {
        final ChatState state = userService.getState(update);
        if (state == COLLECTION_RENAMING) {
            handleRenameCollection(update);
        } else if (state == COLLECTION_ITEM_RENAMING) {
            handleRenameItem(update);
        } else if (isItemRename(update)) {
            handleRenameItemStart(update);
        } else {
            handleRenameCollectionStart(update);
        }
    }

    private void handleRenameItem(Update update) {
        final String newName = update.getMessage().getText();
        if (StringUtils.isEmpty(newName)) {
            // todo, add some error message, etc
            return;
        }

        final CollectionItemEntity item = getItem(update);
        item.setName(newName);

        itemsRepository.save(item);
        userService.setState(update, ChatState.STARTED);

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Имя элемента изменено на " + newName)
                .build()));

        itemHandler.showItem(update, item);
    }

    private void handleRenameItemStart(Update update) {
        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Введите новое имя элемента")
                .replyMarkup(ReplyKeyboardRemove.builder().build())
                .build()));

        userService.setState(update, COLLECTION_ITEM_RENAMING);
    }

    private void handleRenameCollectionStart(Update update) {
        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Введите новое имя коллекции")
                .replyMarkup(ReplyKeyboardRemove.builder().build())
                .build()));

        userService.setState(update, COLLECTION_RENAMING);
    }

    private void handleRenameCollection(Update update) {
        final String newName = update.getMessage().getText();
        if (StringUtils.isEmpty(newName)) {
            // todo, add some error message, etc
            return;
        }

        final CollectionEntity collection = getCollection(update);
        collection.setName(newName);

        collectionRepository.save(collection);
        userService.setState(update, ChatState.STARTED);

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Имя коллекции изменено на " + newName)
                .build()));

        listHandler.showCollectionContent(update, collection);
    }

    private CollectionEntity getCollection(Update update) {
        final CurrentItemContext context = userService.getContext(update, CurrentItemContext.class);
        return collectionRepository.findById(context.getCollectionId())
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));
    }

    private CollectionItemEntity getItem(Update update) {
        final CurrentItemContext context = userService.getContext(update, CurrentItemContext.class);
        return itemsRepository.findById(context.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }

    private boolean isItemRename(Update update) {
        final CurrentItemContext context = userService.getContext(update, CurrentItemContext.class);
        return context.getItemId() != null;
    }
}
