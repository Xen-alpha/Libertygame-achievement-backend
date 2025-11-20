package org.libertymedia.libertyachievement.achievement;

import org.libertymedia.libertyachievement.achievement.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Optional<Achievement> findByAtitle(String title);
    @Query(value="SELECT a FROM Achievement a WHERE a.createdBy = :createdBy")
    List<Achievement> findByCreatedBy(@Param("createdBy") String createdBy); // 딱히 파라미터 이름이라고 명시를 할 필요는 없는 것 같지만...
    List<Achievement> findByGameName(String gameName);
}
