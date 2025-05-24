package dev.abarmin.telegram.collector.handler.command.shared;

import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import dev.abarmin.telegram.collector.handler.command.StateHandler;
import dev.abarmin.telegram.collector.handler.command.collection.ListCommandHandler;
import dev.abarmin.telegram.collector.handler.command.context.CurrentItemContext;
import dev.abarmin.telegram.collector.handler.command.item.ListItemsCallbackHandler;
import dev.abarmin.telegram.collector.handler.command.item.ViewItemCallbackHandler;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.service.ChatState;
import dev.abarmin.telegram.collector.service.CollectionItemService;
import dev.abarmin.telegram.collector.service.CollectionService;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collection;
import java.util.List;

import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;
import static dev.abarmin.telegram.collector.handler.helper.KeyboardHelper.keyboardRow;
import static dev.abarmin.telegram.collector.service.ChatState.COLLECTION_DELETING;
import static dev.abarmin.telegram.collector.service.ChatState.COLLECTION_ITEM_DELETING;

@Component
@RequiredArgsConstructor
public class DeleteCommandHandler implements CommandHandler, StateHandler {

    public static final String DELETE_COMMAND = "❌ Удалить";
    private static final String YES = "Да";
    private static final String NO = "Нет";

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final CollectionItemService collectionItemService;
    private final CollectionService collectionService;
    private final ListCommandHandler listCollectionsHandler;
    private final ListItemsCallbackHandler listHandler;
    private final ViewItemCallbackHandler itemHandler;

    @Override
    public Collection<String> commands() {
        return List.of(DELETE_COMMAND);
    }

    @Override
    public Collection<ChatState> states() {
        return List.of(COLLECTION_DELETING, COLLECTION_ITEM_DELETING);
    }

    @Override
    public void handle(Update update) {
        final ChatState state = userService.getState(update);
        if (state == COLLECTION_DELETING) {
            handleDeleteCollection(update);
        } else if (state == COLLECTION_ITEM_DELETING) {
            handleDeleteItem(update);
        } else if (isItemDelete(update)) {
            handleDeleteItemStart(update);
        } else {
            handleDeleteCollectionStart(update);
        }
    }

    private void handleDeleteItemStart(Update update) {
        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Вы действительно хотите элемент?")
                .replyMarkup(ReplyKeyboardMarkup.builder()
                        .resizeKeyboard(true)
                        .keyboardRow(keyboardRow(YES, NO))
                        .build())
                .build()));

        userService.setState(update, COLLECTION_ITEM_DELETING);
    }

    private void handleDeleteCollectionStart(Update update) {
        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Вы действительно хотите удалить коллекцию?")
                .replyMarkup(ReplyKeyboardMarkup.builder()
                        .resizeKeyboard(true)
                        .keyboardRow(keyboardRow(YES, NO))
                        .build())
                .build()));

        userService.setState(update, COLLECTION_DELETING);
    }

    private void handleDeleteItem(Update update) {
        final String text = update.getMessage().getText();
        final CollectionItemEntity item = getItem(update);
        final CollectionEntity collection = getCollection(update);
        userService.setState(update, ChatState.STARTED);

        if (StringUtils.endsWith(text, YES)) {
            collectionItemService.markDeleted(item);

            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text("Элемент помещен в корзину")
                    .build()));

            listHandler.showCollectionContent(update, collection);
        } else {
            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text("Ну и океюшки")
                    .build()));

            itemHandler.showItem(update, item);
        }
    }

    private void handleDeleteCollection(Update update) {
        final String text = update.getMessage().getText();
        final CollectionEntity collection = getCollection(update);
        userService.setState(update, ChatState.STARTED);

        if (StringUtils.endsWith(text, YES)) {
            collectionService.markDeleted(collection);

            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text("Коллекция помещена в корзину")
                    .build()));

            listCollectionsHandler.handle(update);
        } else {
            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text("Ну и океюшки")
                    .build()));

            listHandler.showCollectionContent(update, collection);
        }
    }

    private CollectionEntity getCollection(Update update) {
        final CurrentItemContext context = userService.getContext(update, CurrentItemContext.class);
        return collectionService.findById(context.getCollectionId())
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));
    }

    private CollectionItemEntity getItem(Update update) {
        final CurrentItemContext context = userService.getContext(update, CurrentItemContext.class);
        return collectionItemService.findById(context.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }

    private boolean isItemDelete(Update update) {
        final CurrentItemContext context = userService.getContext(update, CurrentItemContext.class);
        return context.getItemId() != null;
    }
}
