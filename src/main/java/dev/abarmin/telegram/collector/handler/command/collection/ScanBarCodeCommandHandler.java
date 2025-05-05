package dev.abarmin.telegram.collector.handler.command.collection;

import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import dev.abarmin.telegram.collector.handler.helper.UpdateHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collection;
import java.util.List;

import static dev.abarmin.telegram.collector.handler.command.StartCommandHandler.BOT_MENU_COMMAND;
import static dev.abarmin.telegram.collector.handler.helper.ExceptionHelper.wrap;
import static dev.abarmin.telegram.collector.handler.helper.KeyboardHelper.keyboardRow;

@Component
@RequiredArgsConstructor
public class ScanBarCodeCommandHandler implements CommandHandler {

    public static final String SCAN_BAR_CODE_COMMAND = "\uD83D\uDCF7 Сканировать штрих-код";

    private final TelegramClient telegramClient;

    @Override
    public Collection<String> commands() {
        return List.of(SCAN_BAR_CODE_COMMAND);
    }

    @Override
    public void handle(Update update) {
        wrap(() -> telegramClient.execute(SendMessage.builder()
                .text("❌ Эта функция ещё не реализована")
                .chatId(UpdateHelper.getChatId(update))
                .replyMarkup(ReplyKeyboardMarkup.builder()
                        .resizeKeyboard(true)
                        .keyboardRow(keyboardRow(BOT_MENU_COMMAND))
                        .build())
                .build()));
    }
}
