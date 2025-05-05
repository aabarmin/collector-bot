package dev.abarmin.telegram.collector.service;

import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import dev.abarmin.telegram.collector.domain.UserEntity;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ItemService {

    private static final String SEARCH_QUERY = """
            select ci.*
            from "COLLECTION_ITEMS" as ci
            inner join "COLLECTIONS" c on c."ID" = ci."COLLECTION_ID"
            where c."USER_ID" = :userId and LOWER(ci."NAME") like :keyword
            """;

    private final JdbcClient jdbcClient;

    public Collection<CollectionItemEntity> searchByKeyword(UserEntity user, @NonNull String keyword) {
        return jdbcClient.sql(SEARCH_QUERY)
                .param("userId", user.getId())
                .param("keyword", "%" + keyword.toLowerCase(Locale.ROOT) + "%")
                .query(CollectionItemEntity.class)
                .list();
    }

}
