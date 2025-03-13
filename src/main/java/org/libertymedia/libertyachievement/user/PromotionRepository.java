package org.libertymedia.libertyachievement.user;

import org.libertymedia.libertyachievement.user.model.Promotion;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    public Optional<Promotion> findByUser(UserInfo user);
}
