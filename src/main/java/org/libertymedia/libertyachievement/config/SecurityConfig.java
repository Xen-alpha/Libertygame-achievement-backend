package org.libertymedia.libertyachievement.config;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.config.filter.JwtFilter;
import org.libertymedia.libertyachievement.config.filter.LoginFilter;
import org.libertymedia.libertyachievement.user.LibertyOAuth2UserService;
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
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration configuration;
    private final LibertyOAuth2UserService oAuth2UserService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.oauth2Login(config->{
            config.userInfoEndpoint(endpoint ->
                    endpoint.userService(oAuth2UserService));
        });

        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.sessionManagement(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);
        http.logout(logout -> logout
                .permitAll().logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("ATOKEN").invalidateHttpSession(true)
        );

        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers("/achievement/v0/list/**").permitAll()
                        .requestMatchers("/achievement/v0/addition", "/user/*").hasRole("USER")
                        .anyRequest().authenticated()
        );

        http.addFilterAt(new LoginFilter(configuration.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
