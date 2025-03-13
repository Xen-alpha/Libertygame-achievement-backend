package org.libertymedia.libertyachievement.user;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.model.Promotion;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;

    @Transactional
    public void promotionAccepted(Long userIdx) {
        UserInfo targetUser = userRepository.findById(userIdx).orElse(null);
        if (targetUser == null) {
            throw new RuntimeException("User not found");
        } else {
            targetUser.setRole("ADVANCED");
            userRepository.save(targetUser);
            promotionRepository.delete(targetUser.getPromotion());
        }
    }

    @Transactional
    public void promotionRejected(Long userIdx) {
        UserInfo targetUser = userRepository.findById(userIdx).orElse(null);
        if (targetUser == null) {
            throw new RuntimeException("User not found");
        } else {
            userRepository.save(targetUser);
            promotionRepository.delete(targetUser.getPromotion());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> result = userRepository.findByUsername(username);

        if (result.isPresent()) {
            // 7번 로직
            UserInfo user = result.get();
            return user;
        }

        return null;
    }
}
