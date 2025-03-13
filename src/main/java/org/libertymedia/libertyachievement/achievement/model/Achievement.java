package org.libertymedia.libertyachievement.achievement.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(unique=true, nullable=false)
    private String title;
    @Column(unique=true, nullable=false)
    private String description;
    @Column(nullable=false)
    private Integer maxProgress;

    @OneToMany(mappedBy = "achievement")
    private List<Progress> userProgresses;
}
