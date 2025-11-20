package org.libertymedia.libertyachievement.achievement;

import org.libertymedia.libertyachievement.achievement.model.Achievement;
import org.libertymedia.libertyachievement.achievement.model.Progress;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    Optional<Progress> findByAchievementAndUser(Achievement achievement, UserInfo user);
}
