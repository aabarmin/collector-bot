package dev.abarmin.telegram.collector.handler.command.collection;

import com.google.common.collect.Lists;
import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.UserEntity;
import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import dev.abarmin.telegram.collector.handler.command.SearchCommandHandler;
import dev.abarmin.telegram.collector.handler.command.StartCommandHandler;
import dev.abarmin.telegram.collector.handler.command.item.ListItemsCallbackHandler;
import dev.abarmin.telegram.collector.handler.helper.KeyboardHelper;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.service.CollectionService;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collection;
import java.util.List;

import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;
import static dev.abarmin.telegram.collector.handler.helper.KeyboardHelper.keyboardRow;

@Component
@RequiredArgsConstructor
public class ListCommandHandler implements CommandHandler {

    public static final String MANAGE_COLLECTIONS = "\uD83D\uDCDA Управление коллекциями";
    public static final String COLLECTIONS_LIST = "⬅\uFE0F  К списку коллекций";

    private final CollectionService collectionService;
    private final UserService userService;
    private final TelegramClient telegramClient;

    @Override
    public Collection<String> commands() {
        return List.of(MANAGE_COLLECTIONS, COLLECTIONS_LIST);
    }

    @Override
    public void handle(Update update) {
        final List<InlineKeyboardRow> rows = getCollectionsAsRows(update);

        // showing a list of collection
        if (rows.isEmpty()) {
            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text("У вас пока нет коллекций. Добавим новую?")
                    .build()));
        } else {
            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text("Выберите коллекцию, чтобы посмотреть содержимое:")
                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build())
                    .build()));
        }

        // show a new keyboard
        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Что дальше?")
                .replyMarkup(ReplyKeyboardMarkup.builder()
                        .resizeKeyboard(true)
                        .keyboardRow(KeyboardHelper.keyboardRow(SearchCommandHandler.SEARCH_COMMAND))
                        .keyboardRow(keyboardRow(CreateCommandHandler.CREATE_COLLECTION_COMMAND))
                        .keyboardRow(KeyboardHelper.keyboardRow(StartCommandHandler.BOT_MENU_COMMAND))
                        .build())
                .build()));
    }

    private List<InlineKeyboardRow> getCollectionsAsRows(Update update) {
        final UserEntity user = userService.getUser(update);
        final List<CollectionEntity> collections = collectionService.getAvailableCollections(user);
        final List<List<CollectionEntity>> partitions = Lists.partition(collections, 2);

        return partitions.stream()
                .map(part -> {
                    final InlineKeyboardRow row = new InlineKeyboardRow();
                    part.forEach(collection -> {
                        final InlineKeyboardButton button = InlineKeyboardButton.builder()
                                .text(collection.getName())
                                .callbackData(ListItemsCallbackHandler.callbackData(collection))
                                .build();
                        row.add(button);
                    });
                    return row;
                })
                .toList();
    }
}