package org.libertymedia.libertyachievement.config;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.LibertyOAuth2UserService;
import org.libertymedia.libertyachievement.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration configuration;
    private final UserRepository userRepository;
    private final LibertyOAuth2UserService userService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception { // 세션 방식 로그인

        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/achievement/v1/list/**", "/login", "/logout", "/", "/swagger-ui/index.html").permitAll()
                        .requestMatchers("/achievement/v1/achieve", "/achievement/v1/edit", "/achievement/v1/rate","/achievement/v1/talk", "/achievement/v1/file", "/achievement/v1/game/**", "/user").hasRole("BASIC")
                        .requestMatchers("/achievement/v1/achieve","/achievement/v1/addtion","/achievement/v1/deletion", "/achievement/v1/edit", "/achievement/v1/rate","/achievement/v1/talk", "/achievement/v1/file", "/achievement/v1/game/**", "/user").hasRole("ADVANCED")
                        .anyRequest().authenticated()
        ).oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(userService)
                )
        ).logout(l -> l.logoutSuccessUrl("/logout"));

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
