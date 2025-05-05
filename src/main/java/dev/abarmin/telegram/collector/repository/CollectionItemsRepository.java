package dev.abarmin.telegram.collector.repository;

import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;

public interface CollectionItemsRepository extends
        ListCrudRepository<CollectionItemEntity, Integer>,
        ListPagingAndSortingRepository<CollectionItemEntity, Integer> {

    @Deprecated
    default List<CollectionItemEntity> findByCollectionId(int collectionId) {
        return findByCollectionId(collectionId, Pageable.unpaged(Sort.by(Sort.Direction.ASC, "name")))
                .toList();
    }

    Page<CollectionItemEntity> findByCollectionId(int collectionId, Pageable page);
}
