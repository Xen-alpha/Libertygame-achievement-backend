package org.libertymedia.libertyachievement.achievement.model.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="도전과제 생성 요청")
public class AchievementRequest {
    @Schema(description="만들 도전과제 이름")
    private String title;
    @Schema(description="도전과제에 관한 설명")
    private String description;
    @Schema(description="달성까지 필요한 동작 수행 횟수, 1이면 동작 즉시 달성됩니다.")
    private Integer maxProgress;
    @Schema(description="요청한 사용자 이름")
    private String createdBy;
}
