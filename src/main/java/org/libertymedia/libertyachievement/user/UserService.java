package org.libertymedia.libertyachievement.user;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.model.UserDocument;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserDocument> result = userRepository.findByUsername(username);

        if (result.isPresent()) {
            // 7번 로직
            UserDocument user = result.get();
            return user;
        }

        return null;
    }
}
