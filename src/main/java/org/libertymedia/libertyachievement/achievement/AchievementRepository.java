package org.libertymedia.libertyachievement.achievement;

import org.libertymedia.libertyachievement.achievement.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    public Optional<Achievement> findByTitle(String title);
}
