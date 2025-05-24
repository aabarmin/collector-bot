package dev.abarmin.telegram.collector.repository;

import dev.abarmin.telegram.collector.domain.CollectionAccessEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionAccessRepository extends ListCrudRepository<CollectionAccessEntity, Integer> {

    List<CollectionAccessEntity> findByCollectionId(int collectionId);

    List<CollectionAccessEntity> findByUserId(int userId);

    /**
     * Used to find an owner of the collection by shared collection ID and the user that the collection is shared with.
     *
     * @param collectionId ID of the collection.
     * @param userId ID of the user that the collection is shared with.
     */
    Optional<CollectionAccessEntity> findByCollectionIdAndUserId(int collectionId, int userId);
}
