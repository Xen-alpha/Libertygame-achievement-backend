package org.libertymedia.libertyachievement.user;

import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByUsername(String username);
}
