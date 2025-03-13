package org.libertymedia.libertyachievement.config;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.config.filter.JwtFilter;
import org.libertymedia.libertyachievement.config.filter.LoginFilter;
import org.libertymedia.libertyachievement.user.LibertyOAuth2UserService;
import org.libertymedia.libertyachievement.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

        http.oauth2Login(config->{
            config.loginPage("/user/login").userInfoEndpoint(endpoint ->
                    endpoint.userService(oAuth2UserService));
        });

        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.logout(logout -> logout
                .permitAll().logoutUrl("/logout").logoutSuccessUrl("/user/logout").deleteCookies("ATOKEN").invalidateHttpSession(true)
        );

        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/achievement/v0/list/**", "/user/login", "/logout", "/user/logout").permitAll()
                        .requestMatchers("/achievement/v0/achieve", "/user").hasRole("BASIC")
                        .requestMatchers("/achievement/v0/achieve","/achievement/v0/addtion","/achievement/v0/deletion", "/user").hasRole("ADVANCED")
                        .requestMatchers("/achievement/v0/achieve","/achievement/v0/addtion","/achievement/v0/deletion", "/user","/user/promote/accepted", "/user/promote/declined").hasRole("ADMIN")
                        .anyRequest().authenticated()
        );

        http.addFilterAt(new LoginFilter(configuration.getAuthenticationManager(), userRepository), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
