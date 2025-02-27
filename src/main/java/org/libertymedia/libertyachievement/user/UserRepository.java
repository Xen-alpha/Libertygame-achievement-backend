package org.libertymedia.libertyachievement.user;

import org.libertymedia.libertyachievement.user.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
