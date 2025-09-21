package org.libertymedia.libertyachievement.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libertymedia.libertyachievement.user.UserRepository;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.libertymedia.libertyachievement.util.JwtUtil;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.debug("No cookies found");
            filterChain.doFilter(request, response);
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("AccessTOKEN")) {
                token = cookie.getValue();
            } else if (cookie.getName().equals("libertyUserName")) {
                username = cookie.getValue();
            }
        }
        // Get user info and make authentication class
        if (token != null) {
            UserInfo user = JwtUtil.getUser(token);
            if (user != null) {
                Instant now = Instant.now();
                Instant expires = user.getExpiresAt().toInstant();
                if (now.isBefore(expires)) {
                    UsernamePasswordAuthenticationToken identityToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    identityToken.setDetails(user);
                    SecurityContextHolder.getContext().setAuthentication(identityToken);
                    log.info("user {} authenticated", user.getIdx());
                } else {
                    log.info("user {} expired", user.getIdx());
                    filterChain.doFilter(request, response);
                }
            }
            else {
                log.info("no user");
                filterChain.doFilter(request, response);
            }
        } else if (username != null) {
            UserInfo user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                UserInfo RTokenInfo = JwtUtil.getUser(user.getRefreshToken());
                Instant now = Instant.now();
                Instant expires = Objects.requireNonNull(RTokenInfo).getExpiresAt().toInstant();
                if (now.isBefore(expires)) {
                    String newToken = JwtUtil.generateToken(user.getUserIdx(), user.getUsername(), user.getEmail(), user.getRole(), user.getNotBlocked());
                    user.setPassword(newToken); // 액세스 토큰 저장
                    user.setExpiresAt(ZonedDateTime.now().plusHours(12));
                    userRepository.save(user);
                    // 쿠키에 다시 토큰 설정
                    ResponseCookie cookie = ResponseCookie
                            .from("AccessTOKEN", newToken)
                            .path("/")
                            .httpOnly(true)
                            .secure(true)
                            .maxAge(Duration.ofHours(12L))
                            .build();
                    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                    UsernamePasswordAuthenticationToken identityToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    identityToken.setDetails(user);
                    SecurityContextHolder.getContext().setAuthentication(identityToken);
                    log.info("user {} refreshed", user.getIdx());
                }
            } else {
                log.info("no user info");
                filterChain.doFilter(request, response);
            }
        }
        else {
            log.info("no token, access to anonymous user");
            filterChain.doFilter(request, response);
        }
    }

}
