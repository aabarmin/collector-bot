package dev.abarmin.telegram.collector.service;

import dev.abarmin.telegram.collector.CollectorBotConfiguration;
import dev.abarmin.telegram.collector.domain.AccessPermissions;
import dev.abarmin.telegram.collector.domain.CollectionAccessEntity;
import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.CollectionShareCodeEntity;
import dev.abarmin.telegram.collector.domain.UserEntity;
import dev.abarmin.telegram.collector.repository.CollectionAccessRepository;
import dev.abarmin.telegram.collector.repository.CollectionShareCodeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@RequiredArgsConstructor
public class AccessService {

    private static final Duration VALID_FOR = Duration.ofMinutes(30);

    private final CollectionShareCodeRepository codeRepository;
    private final CollectionAccessRepository accessRepository;
    private final CollectorBotConfiguration configuration;

    public CollectionShareCodeEntity createShareCode(UserEntity user, CollectionEntity collection) {
        final String code = RandomStringUtils.randomAlphabetic(10);
        final CollectionShareCodeEntity entity = CollectionShareCodeEntity.builder()
                .collectionId(collection.getId())
                .userId(user.getId())
                .accessLevel(AccessPermissions.READER)
                .code(code)
                .accessLink(createLink(code))
                .createdAt(Instant.now())
                .validBefore(Instant.now().plus(VALID_FOR))
                .isValid(true)
                .build();

        return codeRepository.save(entity);
    }

    public boolean grantAccess(final UserEntity grantee, final @NonNull CollectionShareCodeEntity code) {
        checkArgument(code.isValid(Instant.now()), "Code is not valid");

        code.setValid(false);
        codeRepository.save(code);

        accessRepository.save(CollectionAccessEntity.builder()
                .collectionId(code.getCollectionId())
                .ownerId(code.getUserId())
                .userId(grantee.getId())
                .permissions(code.getAccessLevel())
                .build());

        return true;
    }

    private String createLink(String code) {
        return "https://t.me/%s?start=%s".formatted(
                configuration.getBots().getCollector().getName(),
                code);
    }

}
