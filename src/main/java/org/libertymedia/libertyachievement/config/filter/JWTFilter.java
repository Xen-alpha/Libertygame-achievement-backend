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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.libertymedia.libertyachievement.util.JwtUtil;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            log.info("No cookies found");
            filterChain.doFilter(request, response);
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("AccessTOKEN")) {
                token = cookie.getValue();
                break;
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
                }
            }
            else {
                log.info("no user");
                filterChain.doFilter(request, response);
            }
        }
        else {
            log.info("no token, access to anonymous user");
            filterChain.doFilter(request, response);
        }
    }
}
