package org.libertymedia.libertyachievement.achievement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AchievementResponse {
    private String title;
    private String description;
    private Integer progress;
    private Integer maxprogress;
}
