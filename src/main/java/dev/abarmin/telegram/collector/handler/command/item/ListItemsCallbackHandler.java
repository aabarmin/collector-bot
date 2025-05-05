package dev.abarmin.telegram.collector.handler.command.item;

import com.google.common.collect.Lists;
import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import dev.abarmin.telegram.collector.handler.command.CallbackHandler;
import dev.abarmin.telegram.collector.handler.command.context.CurrentItemContext;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.repository.CollectionItemsRepository;
import dev.abarmin.telegram.collector.repository.CollectionRepository;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static dev.abarmin.telegram.collector.handler.command.collection.ListCommandHandler.COLLECTIONS_LIST;
import static dev.abarmin.telegram.collector.handler.command.collection.ScanBarCodeCommandHandler.SCAN_BAR_CODE_COMMAND;
import static dev.abarmin.telegram.collector.handler.command.collection.AccessListCommandHandler.MANAGE_COLLECTION_ACCESS;
import static dev.abarmin.telegram.collector.handler.command.item.CreateItemCommandHandler.CREATE_ITEM_COMMAND;
import static dev.abarmin.telegram.collector.handler.command.shared.DeleteCommandHandler.DELETE_COMMAND;
import static dev.abarmin.telegram.collector.handler.command.shared.RenameCommandHandler.RENAME_COMMAND;
import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;
import static dev.abarmin.telegram.collector.handler.helper.KeyboardHelper.keyboardRow;

@Component
@RequiredArgsConstructor
public class ListItemsCallbackHandler implements CallbackHandler {

    public static final String LIST_COLLECTION_ITEMS = "collection_content";

    private final CollectionItemsRepository itemsRepository;
    private final CollectionRepository collectionRepository;
    private final TelegramClient telegramClient;
    private final UserService userService;

    public static String callbackData(CollectionEntity collection) {
        // collection_content:<collectionId>_<page>
        return callbackData(collection, 0, false);
    }

    private static String callbackData(CollectionEntity collection, int page, boolean isInline) {
        return LIST_COLLECTION_ITEMS + ":" + collection.getId() + "_" + page + (isInline ? "_inline" : "");

    }

    private static Update withCallback(Update update, CollectionEntity collection) {
        final CallbackQuery query = new CallbackQuery();
        query.setData(ListItemsCallbackHandler.callbackData(collection));
        update.setCallbackQuery(query);
        return update;
    }

    @Override
    public String callback() {
        return LIST_COLLECTION_ITEMS;
    }

    public void showCollectionContent(Update update, CollectionEntity collection) {
        final Update callbackUpdate = withCallback(update, collection);
        handle(callbackUpdate);
    }

    @Override
    public void handle(Update update) {
        final int collectionId = getCollectionId(update);
        final Pageable request = PageRequest.ofSize(10)
                .withPage(getPage(update))
                .withSort(Sort.by(Sort.Direction.ASC, "name"));

        final CollectionEntity collection = collectionRepository.findById(collectionId).orElseThrow();
        final Page<CollectionItemEntity> page = itemsRepository.findByCollectionId(collectionId, request);

        userService.setContext(update, CurrentItemContext.builder()
                .collectionId(collectionId)
                .build());

        if (page.isEmpty()) {
            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text("В коллекции <strong>" + collection.getName() + "</strong> пока ничего нет. Добавим?")
                    .parseMode("HTML")
                    .replyMarkup(ReplyKeyboardMarkup.builder()
                            .resizeKeyboard(true)
                            .keyboardRow(keyboardRow(CREATE_ITEM_COMMAND, SCAN_BAR_CODE_COMMAND))
                            .keyboardRow(keyboardRow(RENAME_COMMAND, DELETE_COMMAND))
                            .keyboardRow(keyboardRow(COLLECTIONS_LIST, MANAGE_COLLECTION_ACCESS))
                            .build())
                    .build()));
        } else {
            final List<InlineKeyboardRow> rows = getRows(page, collection);
            final String text = "Содержимое коллекции <strong>" + collection.getName() + "</strong>";

            if (isInlineNavigation(update)) {
                wrap(() -> telegramClient.execute(EditMessageText.builder()
                        .text(text)
                        .parseMode("HTML")
                        .chatId(UpdateHelper.getChatId(update))
                        .messageId(UpdateHelper.getMessageId(update))
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build())
                        .build()));
            } else {
                wrap(() -> telegramClient.execute(SendMessage.builder()
                        .text(text)
                        .parseMode("HTML")
                        .chatId(UpdateHelper.getChatId(update))
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build())
                        .build()));

                wrap(() -> telegramClient.execute(SendMessage.builder()
                        .text("Что дальше?")
                        .chatId(UpdateHelper.getChatId(update))
                        .replyMarkup(ReplyKeyboardMarkup.builder()
                                .resizeKeyboard(true)
                                .keyboardRow(keyboardRow(CREATE_ITEM_COMMAND, SCAN_BAR_CODE_COMMAND))
                                .keyboardRow(keyboardRow(RENAME_COMMAND, DELETE_COMMAND))
                                .keyboardRow(keyboardRow(COLLECTIONS_LIST, MANAGE_COLLECTION_ACCESS))
                                .build())
                        .build()));
            }
        }
    }

    private List<InlineKeyboardRow> getRows(Page<CollectionItemEntity> page, CollectionEntity collection) {
        final List<InlineKeyboardRow> rows = Lists.newArrayList();
        final List<List<CollectionItemEntity>> parts = Lists.partition(page.toList(), 2);
        for (List<CollectionItemEntity> part : parts) {
            final InlineKeyboardRow row = new InlineKeyboardRow();
            for (CollectionItemEntity item : part) {
                row.add(InlineKeyboardButton.builder()
                        .text(item.getName())
                        .callbackData(ViewItemCallbackHandler.callbackData(item))
                        .build());
            }
            rows.add(row);
        }
        rows.add(getPaginationRow(page, collection));
        return rows;
    }

    private InlineKeyboardRow getPaginationRow(Page<CollectionItemEntity> page, CollectionEntity collection) {
        final InlineKeyboardRow row = new InlineKeyboardRow();
        if (page.hasPrevious()) {
            row.add(InlineKeyboardButton.builder()
                    .text("◀️")
                    .callbackData(callbackData(collection, page.getNumber() - 1, true))
                    .build());
        } else {
            row.add(InlineKeyboardButton.builder()
                    .text("⏹\uFE0F")
                    .callbackData(callbackData(collection, 0, true))
                    .build());
        }
        row.add(InlineKeyboardButton.builder()
                .text((page.getNumber() + 1) + " из " + page.getTotalPages())
                .callbackData(callbackData(collection, page.getNumber(), true))
                .build());
        if (page.hasNext()) {
            row.add(InlineKeyboardButton.builder()
                    .text("▶️")
                    .callbackData(callbackData(collection, page.getNumber() + 1, true))
                    .build());
        } else {
            row.add(InlineKeyboardButton.builder()
                    .text("⏹\uFE0F")
                    .callbackData(callbackData(collection, page.getTotalPages() - 1, true))
                    .build());
        }
        return row;
    }

    private int getCollectionId(Update update) {
        final String callbackData = UpdateHelper.getCallbackData(update);
        String collectionIdString = StringUtils.substringAfter(callbackData, ":");
        collectionIdString = StringUtils.substringBefore(collectionIdString, "_");

        return Integer.parseInt(collectionIdString);
    }

    private int getPage(Update update) {
        final String callbackData = UpdateHelper.getCallbackData(update);
        String pageString = StringUtils.substringAfter(callbackData, LIST_COLLECTION_ITEMS + ":");
        pageString = StringUtils.substringAfter(pageString, "_");
        pageString = StringUtils.substringBefore(pageString, "_");

        return Integer.parseInt(pageString);
    }

    private boolean isInlineNavigation(Update update) {
        final String callbackData = UpdateHelper.getCallbackData(update);
        return StringUtils.endsWith(callbackData, "_inline");
    }
}
