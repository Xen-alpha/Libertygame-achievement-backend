package org.libertymedia.libertyachievement.achievement.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AchievementResponse {
    private String title;
    private String description;
    private Integer progress;
    private Integer maxprogress;
}
