package dev.abarmin.telegram.collector.fixture;

import dev.abarmin.telegram.collector.SequenceIdGenerator;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@UtilityClass
public class UpdateFixture {

    public static Update messageUpdate(String text, long chatId) {
        final User user = new User(
                SequenceIdGenerator.nextUserId(),
                "Test User",
                false
        );
        user.setUserName("@testUser");

        final Message message = new Message();
        message.setChat(new Chat(chatId, "private"));
        message.setText(text);
        message.setFrom(user);

        final Update update = new Update();
        update.setMessage(message);
        update.setUpdateId(SequenceIdGenerator.nextUpdateId());
        return update;
    }

}
