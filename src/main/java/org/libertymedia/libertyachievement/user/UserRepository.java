package org.libertymedia.libertyachievement.user;

import org.libertymedia.libertyachievement.user.model.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserDocument, Long> {
    Optional<UserDocument> findByUsername(String username);
}
