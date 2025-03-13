package org.libertymedia.libertyachievement.achievement.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QueryResponse {
    private Long userIdx;
    private String username;
    private List<QueryItemResponse> list;
}
