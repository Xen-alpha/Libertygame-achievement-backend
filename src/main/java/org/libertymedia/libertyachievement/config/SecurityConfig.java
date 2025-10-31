package org.libertymedia.libertyachievement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.common.BaseResponse;
import org.libertymedia.libertyachievement.config.filter.AuthFailHandler;
import org.libertymedia.libertyachievement.config.filter.AuthSuccessHandler;
import org.libertymedia.libertyachievement.config.filter.JWTFilter;
import org.libertymedia.libertyachievement.config.filter.LoginRoutingFilter;
import org.libertymedia.libertyachievement.user.LibertyOAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final LibertyOAuth2UserService userService;
    private final JWTFilter jwtFilter;
    private final AuthSuccessHandler authSuccessHandler;
    // private final AuthFailHandler authFailHandler;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception { // 세션 방식 로그인
        http.oauth2Login(oauth2 -> oauth2
                .permitAll().userInfoEndpoint(userInfoEP -> userInfoEP.userService(userService)
                ).successHandler(authSuccessHandler)
        ).csrf(AbstractHttpConfigurer::disable
        ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // v0.5.3에서 난 결론: 세션 아닌 JWT 인증이어야 도전과제 서버가 본 서버와 양립 가능한 것으로 결론을 내림
        ).logout(logout -> logout.permitAll().deleteCookies("AccessTOKEN").logoutSuccessHandler(
            (request, response, authentication) -> {
                response.setStatus(200);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                BaseResponse<String> baseResponse = new BaseResponse<>(true, "로그아웃 성공");
                mapper.writeValue(response.getWriter(), baseResponse);
            })
        ).authorizeHttpRequests( // TODO: REDO request authorization
            (auth) -> auth
                    .requestMatchers("/achievement/v1/achieve", "/achievement/v1/edit", "/achievement/v1/rate","/achievement/v1/talk", "/achievement/v1/file", "/achievement/v1/game/**").hasRole("BASIC")
                    .requestMatchers("/achievement/v1/achieve","/achievement/v1/addition","/achievement/v1/deletion", "/achievement/v1/edit", "/achievement/v1/rate","/achievement/v1/talk", "/achievement/v1/file", "/achievement/v1/game/**").hasRole("ADVANCED")
                    .anyRequest().permitAll()
        );
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(new LoginRoutingFilter(authenticationConfiguration.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(ex -> ex
            .authenticationEntryPoint( (req, res, e) -> {
                logger.info("failed to Authenticate: " + e.getMessage());
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            })
        );

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
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

}
