package org.libertymedia.libertyachievement.achievement;

import org.libertymedia.libertyachievement.achievement.model.AchievementDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AchievementRepository extends MongoRepository<AchievementDocument, Long> {
}
