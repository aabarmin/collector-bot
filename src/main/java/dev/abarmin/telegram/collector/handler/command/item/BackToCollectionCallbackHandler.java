package dev.abarmin.telegram.collector.handler.command.item;

import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.handler.command.CommandHandler;
import dev.abarmin.telegram.collector.handler.command.context.CurrentItemContext;
import dev.abarmin.telegram.collector.repository.CollectionRepository;
import dev.abarmin.telegram.collector.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BackToCollectionCallbackHandler implements CommandHandler {

    public static final String BACK_TO_COLLECTION = "⬅\uFE0F К коллекции";

    private final UserService userService;
    private final CollectionRepository collectionRepository;
    private final ListItemsCallbackHandler listHandler;

    @Override
    public Collection<String> commands() {
        return List.of(BACK_TO_COLLECTION);
    }

    @Override
    public void handle(Update update) {
        final CurrentItemContext itemContext = userService.getContext(update, CurrentItemContext.class);
        final CollectionEntity collection = collectionRepository.findById(itemContext.getCollectionId()).orElseThrow();

        listHandler.showCollectionContent(update, collection);
    }
}
