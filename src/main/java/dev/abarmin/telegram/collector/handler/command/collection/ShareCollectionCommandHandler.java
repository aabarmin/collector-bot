package dev.abarmin.telegram.collector.handler.command.collection;

import dev.abarmin.telegram.collector.domain.AccessPermissions;
import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.CollectionShareCodeEntity;
import dev.abarmin.telegram.collector.domain.UserEntity;
import dev.abarmin.telegram.collector.handler.command.CallbackHandler;
import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import dev.abarmin.telegram.collector.handler.command.context.CurrentItemContext;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.repository.CollectionRepository;
import dev.abarmin.telegram.collector.repository.CollectionShareCodeRepository;
import dev.abarmin.telegram.collector.service.AccessService;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collection;
import java.util.List;

import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;

@Component
@RequiredArgsConstructor
public class ShareCollectionCommandHandler implements CommandHandler, CallbackHandler {

    public static final String SHARE_COLLECTION = "\uD83D\uDD17 Поделиться коллекцией";
    private static final String SHARE_COLLECTION_CALLBACK = "share_collection";
    private static final String SHARE_MESSAGE = """
            Ссылка для доступа к коллекции *%s*
            Уровень доступа: *%s*
            Ссылка действует 30 минут
            ```
            %s
            ```
            """;

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final CollectionRepository collectionRepository;
    private final AccessService accessService;
    private final CollectionShareCodeRepository codeRepository;

    private static String callbackData(CollectionShareCodeEntity code, AccessPermissions accessLevel) {
        return SHARE_COLLECTION_CALLBACK + ":" + code.getCode() + "_" + accessLevel.name();
    }

    @Override
    public Collection<String> commands() {
        return List.of(SHARE_COLLECTION);
    }

    @Override
    public String callback() {
        return SHARE_COLLECTION_CALLBACK;
    }

    @Override
    public void handle(Update update) {
        final CollectionEntity collection = getCollection(update);
        final UserEntity user = userService.getUser(update);

        if (isCallback(update)) {
            final CollectionShareCodeEntity code = getCode(update);
            final AccessPermissions accessLevel = getLevel(update);

            code.setAccessLevel(accessLevel);
            codeRepository.save(code);

            wrap(() -> telegramClient.execute(EditMessageText.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .messageId(UpdateHelper.getMessageId(update))
                    .text(SHARE_MESSAGE.formatted(
                            collection.getName(),
                            accessLevel.getValue(),
                            code.getAccessLink()
                    ))
                    .parseMode("MarkdownV2")
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboardRow(configureButtons(code))
                            .build())
                    .build()));
        } else {
            final CollectionShareCodeEntity code = accessService.createShareCode(user, collection);

            wrap(() -> telegramClient.execute(SendMessage.builder()
                    .chatId(UpdateHelper.getChatId(update))
                    .text(SHARE_MESSAGE.formatted(
                            collection.getName(),
                            code.getAccessLevel().getValue(),
                            code.getAccessLink()
                    ))
                    .parseMode("MarkdownV2")
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboardRow(configureButtons(code))
                            .build())
                    .build()));
        }
    }

    private InlineKeyboardRow configureButtons(CollectionShareCodeEntity code) {
        final InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(InlineKeyboardButton.builder()
                .text("❌Отменить")
                .callbackData(callbackData(code, AccessPermissions.NONE))
                .build());
        row.add(InlineKeyboardButton.builder()
                .text("\uD83D\uDCD5 Только чтение")
                .callbackData(callbackData(code, AccessPermissions.READER))
                .build());
        row.add(InlineKeyboardButton.builder()
                .text("\uD83D\uDCDD Полный доступ")
                .callbackData(callbackData(code, AccessPermissions.FULL_ACCESS))
                .build());
        return row;
    }

    private CollectionEntity getCollection(Update update) {
        final CurrentItemContext context = userService.getContext(update, CurrentItemContext.class);
        return collectionRepository.findById(context.getCollectionId())
                .orElseThrow(() -> new IllegalStateException("Collection not found"));
    }

    private boolean isCallback(Update update) {
        return update.hasCallbackQuery();
    }

    private CollectionShareCodeEntity getCode(Update update) {
        final String callbackData = UpdateHelper.getCallbackData(update);
        String code = StringUtils.substringAfter(callbackData, ":");
        code = StringUtils.substringBefore(code, "_");

        return codeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException("Share code not found"));
    }

    private AccessPermissions getLevel(Update update) {
        final String callbackData = UpdateHelper.getCallbackData(update);
        String level = StringUtils.substringAfter(callbackData, ":");
        level = StringUtils.substringAfter(level, "_");

        return AccessPermissions.valueOf(level);
    }
}
