package org.libertymedia.libertyachievement.achievement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BaseResponse<T> {
    private Boolean success;
    private T result;
}
