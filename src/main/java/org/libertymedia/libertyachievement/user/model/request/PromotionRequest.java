package org.libertymedia.libertyachievement.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description="도전과제 제작자로 전환하는 요청입니다.")
public class PromotionRequest {
    @Schema(description="사용자 이름")
    @NotNull
    private String username;
    @Schema(description="사용자가 승급 요청을 진행할 이메일")
    @NotNull
    @Email
    private String email;
}
