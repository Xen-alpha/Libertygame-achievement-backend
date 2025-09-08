package org.libertymedia.libertyachievement.config;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.config.filter.LoginFilter;
import org.libertymedia.libertyachievement.user.LibertyOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration configuration;
    private final LibertyOAuth2UserService userService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception { // 세션 방식 로그인



        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/achievement/v1/list/**", "/login", "/login/**", "/logout", "/", "/swagger-ui/index.html", "/v3/**").permitAll()
                        .requestMatchers("/achievement/v1/achieve", "/achievement/v1/edit", "/achievement/v1/rate","/achievement/v1/talk", "/achievement/v1/file", "/achievement/v1/game/**", "/user/**").hasRole("BASIC")
                        .requestMatchers("/achievement/v1/achieve","/achievement/v1/addition","/achievement/v1/deletion", "/achievement/v1/edit", "/achievement/v1/rate","/achievement/v1/talk", "/achievement/v1/file", "/achievement/v1/game/**", "/user/**").hasRole("ADVANCED")
                        .anyRequest().authenticated()
        ).oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(userService)
                )
        ).logout(l -> l.logoutSuccessUrl("/user/logout").clearAuthentication(true)
        ).formLogin(AbstractHttpConfigurer::disable
        ).csrf(AbstractHttpConfigurer::disable
        ).addFilterAt(new LoginFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("https://dev.libertygame.work", "https://libertygame.work", "https://libertyga.me", "http://localhost"));
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
