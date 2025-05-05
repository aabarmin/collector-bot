package dev.abarmin.telegram.collector.handler.command.item;

import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import dev.abarmin.telegram.collector.handler.command.CallbackHandler;
import dev.abarmin.telegram.collector.handler.command.context.CurrentItemContext;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.repository.CollectionItemsRepository;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static dev.abarmin.telegram.collector.handler.command.item.AddPhotoCommandHandler.ADD_PHOTO_COMMAND;
import static dev.abarmin.telegram.collector.handler.command.item.BackToCollectionCallbackHandler.BACK_TO_COLLECTION;
import static dev.abarmin.telegram.collector.handler.command.shared.DeleteCommandHandler.DELETE_COMMAND;
import static dev.abarmin.telegram.collector.handler.command.shared.RenameCommandHandler.RENAME_COMMAND;
import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;
import static dev.abarmin.telegram.collector.handler.helper.KeyboardHelper.keyboardRow;

@Component
@RequiredArgsConstructor
public class ViewItemCallbackHandler implements CallbackHandler {

    public static final String VIEW_ITEM = "view_item";

    private final TelegramClient telegramClient;
    private final CollectionItemsRepository itemsRepository;
    private final UserService userService;

    public static String callbackData(CollectionItemEntity item) {
        return VIEW_ITEM + ":" + item.getId();
    }

    private static Update withCallback(Update update, CollectionItemEntity item) {
        final CallbackQuery query = new CallbackQuery();
        query.setData(ViewItemCallbackHandler.callbackData(item));
        update.setCallbackQuery(query);
        return update;
    }

    @Override
    public String callback() {
        return VIEW_ITEM;
    }

    public void showItem(Update update, CollectionItemEntity item) {
        final Update callbackUpdate = withCallback(update, item);
        handle(callbackUpdate);
    }

    @Override
    public void handle(Update update) {
        final String callbackData = UpdateHelper.getCallbackData(update);
        final int itemId = Integer.parseInt(StringUtils.substringAfter(callbackData, ":"));
        final CollectionItemEntity item = itemsRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        userService.setContext(update, CurrentItemContext.builder()
                .itemId(itemId)
                .collectionId(item.getCollectionId())
                .build());

        final ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboardRow(keyboardRow(RENAME_COMMAND, DELETE_COMMAND))
                .keyboardRow(keyboardRow(ADD_PHOTO_COMMAND))
                .keyboardRow(keyboardRow(BACK_TO_COLLECTION))
                .build();

        if (item.hasPhoto()) {
            wrap(() -> telegramClient.execute(SendPhoto.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .caption("Просмотр " + item.getName())
                    .replyMarkup(keyboard)
                    .photo(new InputFile(item.getImageFileId()))
                    .build()));
        } else {
            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text("Просмотр " + item.getName())
                    .replyMarkup(keyboard)
                    .build()));
        }
    }
}
