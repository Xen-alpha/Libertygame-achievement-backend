package org.libertymedia.libertyachievement.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="도전과제 제작자로 전환하는 요청입니다.")
public class PromotionRequest {
    private String username;
}
