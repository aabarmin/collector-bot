package dev.abarmin.telegram.collector.handler.command;

import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import dev.abarmin.telegram.collector.domain.UserEntity;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.service.ChatState;
import dev.abarmin.telegram.collector.repository.CollectionRepository;
import dev.abarmin.telegram.collector.service.ItemService;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collection;
import java.util.List;

import static dev.abarmin.telegram.collector.handler.command.collection.ListCommandHandler.COLLECTIONS_LIST;
import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;
import static dev.abarmin.telegram.collector.handler.helper.KeyboardHelper.keyboardRow;

@Component
@RequiredArgsConstructor
public class SearchCommandHandler implements CommandHandler, StateHandler {

    public static final String SEARCH_COMMAND = "\uD83D\uDD0D Поиск";

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final ItemService itemService;
    private final CollectionRepository collectionRepository;

    @Override
    public Collection<String> commands() {
        return List.of(SEARCH_COMMAND);
    }

    @Override
    public Collection<ChatState> states() {
        return List.of(ChatState.COLLECTION_ITEM_SEARCHING);
    }

    @Override
    public void handle(Update update) {
        final ChatState state = userService.getState(update);
        if (state == ChatState.COLLECTION_ITEM_SEARCHING) {
            handleSearch(update);
        } else {
            handleSearchStart(update);
        }
    }

    private void handleSearchStart(Update update) {
        userService.setState(update, ChatState.COLLECTION_ITEM_SEARCHING);

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Введите текст для поиска")
                .replyMarkup(ReplyKeyboardRemove.builder().build())
                .build()));
    }

    private void handleSearch(Update update) {
        final String query = update.getMessage().getText();
        if (StringUtils.isEmpty(query)) {
            return;
        }

        userService.setState(update, ChatState.STARTED);
        final UserEntity user = userService.getUser(update);
        final Collection<CollectionItemEntity> items = itemService.searchByKeyword(user, query);

        final StringBuilder builder = new StringBuilder();
        if (items.isEmpty()) {
            builder.append("Ничего не нашлось");
        } else {
            builder.append("Нашлось ").append(items.size()).append(" элементов:\n");
            for (CollectionItemEntity item : items) {
                builder.append("- ").append(item.getName())
                        .append(" (").append(getCollection(item).getName()).append(")")
                        .append("\n");
            }
        }

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text(builder.toString())
                .replyMarkup(ReplyKeyboardMarkup.builder()
                        .resizeKeyboard(true)
                        .keyboardRow(keyboardRow(COLLECTIONS_LIST))
                        .build())
                .build()));
    }

    private CollectionEntity getCollection(CollectionItemEntity item) {
        return collectionRepository.findById(item.getCollectionId())
                .orElseThrow(() -> new IllegalArgumentException("Collection not found"));
    }
}
