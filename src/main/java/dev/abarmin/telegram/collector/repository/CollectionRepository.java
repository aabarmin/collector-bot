package dev.abarmin.telegram.collector.repository;

import dev.abarmin.telegram.collector.domain.CollectionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CollectionRepository extends CrudRepository<CollectionEntity, Integer> {

    List<CollectionEntity> findByUserId(int userId);

}
