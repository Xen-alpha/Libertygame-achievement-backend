package org.libertymedia.libertyachievement.achievement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.libertymedia.libertyachievement.achievement.model.Achievement;
import org.libertymedia.libertyachievement.achievement.model.Progress;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AchievementResponse {
    private Long idx;
    private String title;
    private String description;
    private Integer progress;
    private Integer maxprogress;

    public static AchievementResponse from(Achievement achievement, Progress progress) {
        return AchievementResponse.builder().idx(achievement.getIdx()).title(achievement.getAtitle()).description(achievement.getAdescription()).progress(progress.getCurrentProgress()).maxprogress(achievement.getMaxProgress()).build();
    }
}
