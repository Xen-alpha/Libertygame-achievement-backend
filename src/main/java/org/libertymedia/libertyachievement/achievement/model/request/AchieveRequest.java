package org.libertymedia.libertyachievement.achievement.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="도전과제 달성 요청")
public class AchieveRequest {
    @Schema(description="달성한 도전과제")
    private String title;
    @Schema(description="달성한 사용자 이름")
    private String username;
}
