package dev.abarmin.telegram.collector.repository;

import dev.abarmin.telegram.collector.domain.CollectionShareCodeEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface CollectionShareCodeRepository extends ListCrudRepository<CollectionShareCodeEntity, Integer> {

    Optional<CollectionShareCodeEntity> findByCode(String code);

}
