package org.libertymedia.libertyachievement;

import org.libertymedia.libertyachievement.model.AchievementDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AchievementRepository extends MongoRepository<AchievementDocument, Long> {
}
