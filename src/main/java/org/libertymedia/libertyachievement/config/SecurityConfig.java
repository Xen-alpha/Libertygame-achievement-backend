package org.libertymedia.libertyachievement.config;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.config.filter.JwtFilter;
import org.libertymedia.libertyachievement.config.filter.LoginFilter;
import org.libertymedia.libertyachievement.user.LibertyOAuth2UserService;
import org.libertymedia.libertyachievement.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration configuration;
    private final LibertyOAuth2UserService oAuth2UserService;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception { // 세션 방식 로그인

        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/achievement/v1/list/**", "/login").permitAll()
                        .requestMatchers("/achievement/v1/achieve", "/achievement/v1/edit", "/achievement/v1/rate","/achievement/v1/talk", "/achievement/v1/file", "/achievement/v1/game/**", "/user").hasRole("BASIC")
                        .requestMatchers("/achievement/v1/achieve","/achievement/v1/addtion","/achievement/v1/deletion", "/achievement/v1/edit", "/achievement/v1/rate","/achievement/v1/talk", "/achievement/v1/file", "/achievement/v1/game/**", "/user").hasRole("ADVANCED")
                        .requestMatchers("/achievement/v1/achieve","/achievement/v1/addtion","/achievement/v1/deletion", "/user").hasRole("ADMIN")
                        .anyRequest().authenticated()
        ).oauth2Login(Customizer.withDefaults())        // OAuth2 로그인 사용
        .csrf(csrf -> csrf.ignoringRequestMatchers("/achievement/**", "/user/**")) // API는 CSRF 제외
        .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        );

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
