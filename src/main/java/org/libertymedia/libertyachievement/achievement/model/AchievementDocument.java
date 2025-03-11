package org.libertymedia.libertyachievement.achievement.model;


import jakarta.persistence.Column;
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
@Document(collection="achievement")
public class AchievementDocument {
    @Id
    private Long idx;
    @Column(unique=true)
    private String name;
    private String description;
    private Long userId;
    private Integer progress;
    private Integer maxProgress;
}
