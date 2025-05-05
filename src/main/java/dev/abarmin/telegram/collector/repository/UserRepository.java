package dev.abarmin.telegram.collector.repository;

import dev.abarmin.telegram.collector.domain.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {

    Optional<UserEntity> findByChatId(long chatId);

}
