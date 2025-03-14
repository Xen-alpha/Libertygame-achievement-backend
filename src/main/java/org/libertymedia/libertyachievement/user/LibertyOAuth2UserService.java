package org.libertymedia.libertyachievement.user;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.model.LibertyOAuth2User;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LibertyOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    private final DefaultOAuth2UserService defaultOAuth2UserService
            = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String username = (String) attributes.get("username");
        String email = (String) attributes.get("confirmed_email");
        Long idx = (Long) attributes.get("sub");
        Boolean blocked = (Boolean) attributes.get("blocked");
        System.out.println(username + " with email " + email);
        UserInfo userInfo = userRepository.findByUsername(username).orElse(null);
        if (userInfo == null) {
            return new LibertyOAuth2User(UserInfo.builder().userIdx(idx).notBlocked(true).username(username).email(email).password(UUID.randomUUID().toString()).role("BASIC").build());
        }

        return oAuth2User;
    }
}