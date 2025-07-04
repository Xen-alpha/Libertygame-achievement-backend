package org.libertymedia.libertyachievement.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@Schema(description="도전과제 제작자 전환 요청 저장 테이블")
public class Promotion {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idx;

    @OneToOne(mappedBy = "promotion")
    @Schema(description="요청자 정보")
    private UserInfo user;
}
