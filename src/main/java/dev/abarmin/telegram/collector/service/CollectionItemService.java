package dev.abarmin.telegram.collector.service;

import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import dev.abarmin.telegram.collector.domain.UserEntity;
import dev.abarmin.telegram.collector.repository.CollectionItemsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectionItemService {

    private final CollectionItemsRepository repository;

    private static final String SEARCH_QUERY = """
            select ci.*
            from "COLLECTION_ITEMS" as ci
            inner join "COLLECTIONS" c on c."ID" = ci."COLLECTION_ID"
            where c."USER_ID" = :userId and LOWER(ci."NAME") like :keyword
            """;

    private final JdbcClient jdbcClient;

    public Optional<CollectionItemEntity> findById(int id) {
        return repository.findById(id);
    }

    public CollectionItemEntity markDeleted(@NonNull CollectionItemEntity item) {
        item.setDeleted(true);
        return repository.save(item);
    }

    public Collection<CollectionItemEntity> searchByKeyword(UserEntity user, @NonNull String keyword) {
        return jdbcClient.sql(SEARCH_QUERY)
                .param("userId", user.getId())
                .param("keyword", "%" + keyword.toLowerCase(Locale.ROOT) + "%")
                .query(CollectionItemEntity.class)
                .list();
    }

}
