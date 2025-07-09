package org.libertymedia.libertyachievement.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

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

    @Schema(description = "요청 시각, 약 12시간 후 만료시키기 위함")
    private ZonedDateTime requestDate;
    @Schema(description = "요청 허용 여부 기록")
    private Boolean accepted;
}
