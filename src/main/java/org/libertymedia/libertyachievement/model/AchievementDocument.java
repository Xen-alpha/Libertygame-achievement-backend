package org.libertymedia.libertyachievement.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Document
public class AchievementDocument {
    @Id
    private Long idx;
    private String name;
    private String description;
    private Long userId;
    private Integer progress;
    private Integer maxProgress;
}
