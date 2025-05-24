package dev.abarmin.telegram.collector.repository;

import dev.abarmin.telegram.collector.domain.CollectionEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CollectionRepository extends CrudRepository<CollectionEntity, Integer> {

    @Query("SELECT * FROM COLLECTIONS c WHERE c.USER_ID = :userId and c.DELETED = false")
    List<CollectionEntity> findByUserId(int userId);

}
