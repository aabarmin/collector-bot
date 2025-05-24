package dev.abarmin.telegram.collector.service;

import com.google.common.collect.Iterables;
import dev.abarmin.telegram.collector.domain.CollectionAccessEntity;
import dev.abarmin.telegram.collector.domain.CollectionEntity;
import dev.abarmin.telegram.collector.domain.UserEntity;
import dev.abarmin.telegram.collector.repository.CollectionAccessRepository;
import dev.abarmin.telegram.collector.repository.CollectionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final CollectionAccessRepository accessRepository;

    public CollectionEntity markDeleted(@NonNull CollectionEntity collection) {
        collection.setDeleted(true);
        return collectionRepository.save(collection);
    }

    public Optional<CollectionEntity> findById(int id) {
        return collectionRepository.findById(id);
    }

    public List<CollectionEntity> getAvailableCollections(@NonNull UserEntity user) {
        final Collection<CollectionEntity> collections = new HashSet<>();
        // own collections
        collections.addAll(collectionRepository.findByUserId(user.getId()));
        // shared collections
        final List<Integer> sharedCollectionIds = accessRepository.findByUserId(user.getId()).stream()
                .map(CollectionAccessEntity::getCollectionId)
                .toList();
        Iterables.addAll(
                collections,
                collectionRepository.findAllById(sharedCollectionIds)
        );
        return List.copyOf(collections);
    }

}
