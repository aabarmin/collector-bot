package dev.abarmin.telegram.collector.handler.command;

import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.CollectionShareCodeEntity;
import dev.abarmin.telegram.collector.domain.UserEntity;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import dev.abarmin.telegram.collector.repository.CollectionRepository;
import dev.abarmin.telegram.collector.repository.CollectionShareCodeRepository;
import dev.abarmin.telegram.collector.repository.UserRepository;
import dev.abarmin.telegram.collector.service.AccessService;
import dev.abarmin.telegram.collector.service.ChatState;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static dev.abarmin.telegram.collector.handler.command.collection.ListCommandHandler.MANAGE_COLLECTIONS;
import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;
import static dev.abarmin.telegram.collector.handler.helper.KeyboardHelper.keyboardRow;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

    public static final String BOT_MENU_COMMAND = "⬅️ В меню бота";
    public static final String START_COMMAND = "/start";

    private final TelegramClient telegramClient;
    private final UserService userService;
    private final CollectionShareCodeRepository codeRepository;
    private final AccessService accessService;
    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;

    @Override
    public Collection<String> commands() {
        return List.of(START_COMMAND, BOT_MENU_COMMAND);
    }

    @Override
    public void handle(Update update) {
        userService.setState(update, ChatState.STARTED);

        if (hasAccessCode(update)) {
            tryGrantAccess(update);
        }

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Добро пожаловать, выберите действие:")
                .replyMarkup(ReplyKeyboardMarkup.builder()
                        .resizeKeyboard(true)
                        .keyboardRow(keyboardRow(MANAGE_COLLECTIONS))
                        .keyboardRow(keyboardRow("\uD83D\uDC65 Управление доступом"))
                        .build())
                .build()));
    }

    private void tryGrantAccess(Update update) {
        final String code = getAccessCode(update);
        final Optional<CollectionShareCodeEntity> byCode = codeRepository.findByCode(code);
        final String message;
        if (byCode.isEmpty()) {
            message = "Код доступа не найден. Пожалуйста, проверьте его и попробуйте снова.";
        } else {
            final CollectionShareCodeEntity codeEntity = byCode.get();
            if (!codeEntity.isValid(Instant.now())) {
                message = "Код доступа недействителен. Пожалуйста, проверьте его и попробуйте снова.";
            } else {
                final UserEntity currentUser = userService.getUser(update);
                accessService.grantAccess(currentUser, codeEntity);

                final UserEntity owner = userRepository.findById(codeEntity.getUserId()).orElseThrow();
                final CollectionEntity collection = collectionRepository.findById(codeEntity.getCollectionId()).orElseThrow();

                message = "%s предоставил вам доступ к коллекции %s".formatted(
                        owner.getUsername(),
                        collection.getName()
                );
            }
        }

        wrap(() -> telegramClient.execute(SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(message)
                .build()));
    }

    private boolean hasAccessCode(Update update) {
        return StringUtils.isNoneEmpty(getAccessCode(update));
    }

    private String getAccessCode(Update update) {
        final String code = StringUtils.substringAfter(
                UpdateHelper.getText(update),
                START_COMMAND);

        return StringUtils.trimToEmpty(code);
    }
}
