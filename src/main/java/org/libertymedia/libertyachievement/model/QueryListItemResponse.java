package org.libertymedia.libertyachievement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class QueryListItemResponse {
    private String title;
    private String description;
    private Integer progress;
    private Integer maxProgress;
}
