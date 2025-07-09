package org.libertymedia.libertyachievement.achievement.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="도전과제 달성 요청")
public class AchieveRequest {
    @Schema(description="달성한 도전과제")
    @NotNull
    private String title;
    @Schema(description="달성한 사용자 이름")
    @NotNull
    private String username;
}
