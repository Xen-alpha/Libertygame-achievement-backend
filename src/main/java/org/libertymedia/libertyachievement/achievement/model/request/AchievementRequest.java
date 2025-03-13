package org.libertymedia.libertyachievement.achievement.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AchievementRequest {
    private String title;
    private String description;
    private Integer maxProgress;
}
