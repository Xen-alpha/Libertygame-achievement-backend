package org.libertymedia.libertyachievement.achievement.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.libertymedia.libertyachievement.user.model.UserInfo;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Schema(description="도전과제 진행도 기록 테이블")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private Integer currentProgress;

    @ManyToOne
    private Achievement achievement;

    @ManyToOne
    private UserInfo user;
}
