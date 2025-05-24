package dev.abarmin.telegram.collector.repository;

import dev.abarmin.telegram.collector.domain.CollectionItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface CollectionItemsRepository extends
        ListCrudRepository<CollectionItemEntity, Integer>,
        ListPagingAndSortingRepository<CollectionItemEntity, Integer> {

    Page<CollectionItemEntity> findByCollectionIdAndDeleted(int collectionId, boolean deleted, Pageable page);

    default Page<CollectionItemEntity> findByCollectionId(int collectionId, Pageable page) {
        return findByCollectionIdAndDeleted(collectionId, false, page);
    }
}
