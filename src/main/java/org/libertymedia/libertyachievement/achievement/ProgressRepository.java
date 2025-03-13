package org.libertymedia.libertyachievement.achievement;

import org.libertymedia.libertyachievement.achievement.model.Achievement;
import org.libertymedia.libertyachievement.achievement.model.Progress;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    public Progress findByAchievementAndUser(Achievement achievement, UserInfo user);
}
