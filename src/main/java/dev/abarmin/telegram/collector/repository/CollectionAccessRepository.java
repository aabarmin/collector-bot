package dev.abarmin.telegram.collector.repository;

import dev.abarmin.telegram.collector.domain.CollectionAccessEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface CollectionAccessRepository extends ListCrudRepository<CollectionAccessEntity, Integer> {

    List<CollectionAccessEntity> findByCollectionId(int collectionId);

    List<CollectionAccessEntity> findByUserId(int userId);

}
