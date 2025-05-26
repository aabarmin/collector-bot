package dev.abarmin.telegram.collector.handler.command;

import dev.abarmin.telegram.collector.BaseIntegrationTest;
import dev.abarmin.telegram.collector.SequenceIdGenerator;
import dev.abarmin.telegram.collector.TestTelegramMessageCollector;
import dev.abarmin.telegram.collector.TestTelegramUser;
import dev.abarmin.telegram.collector.fixture.UpdateFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static dev.abarmin.telegram.collector.assertion.MessagesAssertion.assertThat;
import static dev.abarmin.telegram.collector.handler.command.StartCommandHandler.MANAGE_ACCESS_COMMAND;
import static dev.abarmin.telegram.collector.handler.command.collection.ListCommandHandler.MANAGE_COLLECTIONS;

class StartCommandHandlerIntTest extends BaseIntegrationTest {

    final long chatId = SequenceIdGenerator.nextChatId();

    @Autowired
    TestTelegramUser client;

    @Autowired
    TestTelegramMessageCollector messageCollector;

    @Test
    void whenStartSend_thenListOfBasicCommandsReceived() {
        final Instant now = Instant.now();
        client.sendUpdate(UpdateFixture.messageUpdate(
                "/start",
                chatId
        ));

        messageCollector.messagesAfter(chatId, now, messages -> {
            assertThat(messages)
                    .hasMessageWithText("Добро пожаловать, выберите действие:")
                    .hasReplyKeyboardButton(MANAGE_COLLECTIONS)
                    .hasReplyKeyboardButton(MANAGE_ACCESS_COMMAND);
        });
    }
}