package dev.abarmin.telegram.collector.handler.command.item;

import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import dev.abarmin.telegram.collector.handler.command.StateHandler;
import dev.abarmin.telegram.collector.handler.command.context.CurrentItemContext;
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

@Component
@RequiredArgsConstructor
public class CreateItemCommandHandler implements CommandHandler, StateHandler {

    public static final String CREATE_ITEM_COMMAND = "➕Добавить";

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final CollectionItemsRepository itemsRepository;
    private final CollectionRepository collectionRepository;
    private final ListItemsCallbackHandler listHandler;

    @Override
    public Collection<String> commands() {
        return List.of(CREATE_ITEM_COMMAND);
    }

    @Override
    public Collection<ChatState> states() {
        return List.of(ChatState.COLLECTION_ITEM_CREATION);
    }

    @Override
    public void handle(Update update) {
        final ChatState state = userService.getState(update);
        if (state == ChatState.COLLECTION_ITEM_CREATION) {
            handleCreateItem(update);
        } else {
            handleCreateItemStart(update);
        }
    }

    private void handleCreateItemStart(Update update) {
        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Введите название")
                .replyMarkup(ReplyKeyboardRemove.builder().build())
                .build()));

        userService.setState(update, ChatState.COLLECTION_ITEM_CREATION);
    }

    private void handleCreateItem(Update update) {
        final String itemName = update.getMessage().getText();
        if (StringUtils.isEmpty(itemName)) {
            // todo, add some error message, etc
            return;
        }

        final CurrentItemContext context = userService.getContext(update, CurrentItemContext.class);
        itemsRepository.save(CollectionItemEntity.builder()
                .collectionId(context.getCollectionId())
                .name(itemName)
                .build());

        userService.setState(update, ChatState.STARTED);
        userService.setContext(update, null);

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Новый элемент коллекции успешно создан: " + itemName)
                .build()));

        final CollectionEntity collection = collectionRepository.findById(context.getCollectionId()).orElseThrow();

        listHandler.showCollectionContent(update, collection);
    }
}
