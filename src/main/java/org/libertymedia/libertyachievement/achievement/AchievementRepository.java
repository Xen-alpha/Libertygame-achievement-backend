package org.libertymedia.libertyachievement.achievement;

import org.libertymedia.libertyachievement.achievement.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Optional<Achievement> findByAtitle(String title);
    List<Achievement> findByCreatedBy(String createdBy);
    List<Achievement> findByGameName(String gameName);
}
