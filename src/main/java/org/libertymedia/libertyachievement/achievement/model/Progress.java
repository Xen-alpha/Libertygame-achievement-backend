package org.libertymedia.libertyachievement.achievement.model;

import jakarta.persistence.*;
import lombok.*;
import org.libertymedia.libertyachievement.user.model.UserInfo;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
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
