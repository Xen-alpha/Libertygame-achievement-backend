package org.libertymedia.libertyachievement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AchieveRequest {
    private String title;
    private String description;
    private Long userId;
}
