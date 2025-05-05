package dev.abarmin.telegram.collector.handler.command.item;

import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import dev.abarmin.telegram.collector.handler.command.StartCommandHandler;
import dev.abarmin.telegram.collector.handler.command.StateHandler;
import dev.abarmin.telegram.collector.handler.command.context.CurrentItemContext;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.service.ChatState;
import dev.abarmin.telegram.collector.repository.CollectionItemsRepository;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;
import static dev.abarmin.telegram.collector.service.ChatState.COLLECTION_ITEM_ADDING_PHOTO;

@Component
@RequiredArgsConstructor
public class AddPhotoCommandHandler implements CommandHandler, StateHandler {

    public static final String ADD_PHOTO_COMMAND = "\uD83D\uDCF7 Добавить фото";

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final CollectionItemsRepository itemsRepository;
    private final ViewItemCallbackHandler itemHandler;
    private final StartCommandHandler startHandler;

    @Override
    public Collection<String> commands() {
        return List.of(ADD_PHOTO_COMMAND);
    }

    @Override
    public Collection<ChatState> states() {
        return List.of(COLLECTION_ITEM_ADDING_PHOTO);
    }

    @Override
    public void handle(Update update) {
        final ChatState state = userService.getState(update);
        if (isStartCommand(update)) {
            // todo, get rid of this hack later on
            startHandler.handle(update);
        } else if (state == COLLECTION_ITEM_ADDING_PHOTO) {
            handleAddPhoto(update);
        } else {
            handleAddPhotoStart(update);
        }
    }

    private boolean isStartCommand(Update update) {
        return Optional.of(update)
                .filter(Update::hasMessage)
                .map(Update::getMessage)
                .filter(Message::hasText)
                .map(Message::getText)
                .map(text -> startHandler.commands().contains(text))
                .orElse(false);
    }

    private void handleAddPhoto(Update update) {
        final List<PhotoSize> photos = Optional.of(update)
                .filter(Update::hasMessage)
                .filter(upd -> upd.getMessage().hasPhoto())
                .map(Update::getMessage)
                .map(Message::getPhoto)
                .orElse(List.of());

        if (photos.isEmpty()) {
            // assuming, nothing to validate and we're looking for a photo not text
            return;
        }
        final PhotoSize photo = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElseThrow();


        final CollectionItemEntity item = getItem(update);
        item.setImageFileId(photo.getFileId());
        itemsRepository.save(item);

        userService.setState(update, ChatState.STARTED);

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Фото обновлено")
                .build()));

        itemHandler.showItem(update, item);
    }

    private void handleAddPhotoStart(Update update) {
        userService.setState(update, COLLECTION_ITEM_ADDING_PHOTO);

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(UpdateHelper.getChatId(update))
                .text("Отправьте фото элемента коллекции")
                .replyMarkup(ReplyKeyboardRemove.builder().build())
                .build()));
    }

    private CollectionItemEntity getItem(Update update) {
        final CurrentItemContext context = userService.getContext(update, CurrentItemContext.class);
        return itemsRepository.findById(context.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }
}
