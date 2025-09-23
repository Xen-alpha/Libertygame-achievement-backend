package org.libertymedia.libertyachievement.user.model.request;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Tag(name= "Common Request Body",description="Body가 있는 일반적인 HTTP 요청은 이 양식을 사용")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommonRequest {
    @NotNull
    String AccessToken;
    Object contents;
}
