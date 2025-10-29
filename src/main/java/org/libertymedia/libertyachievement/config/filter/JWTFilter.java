package org.libertymedia.libertyachievement.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.libertymedia.libertyachievement.user.model.request.CommonRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.libertymedia.libertyachievement.util.JwtUtil;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/login")) {
            response.sendRedirect("/oauth2/authorize/libertygame");
            return;
        }
        Cookie[] cookies = request.getCookies();
        String token = null;
        for (Cookie cookie : cookies) {
            String cookieName = cookie.getName();
            if (cookieName.equals("AccessTOKEN")) {
                token = cookie.getValue();
                break;
            }
        }

        // Get user info and make authentication class
        if (token != null) {
            try {
                UserInfo user = JwtUtil.getUser(token);
                if (user != null) {
                    UsernamePasswordAuthenticationToken identityToken = new UsernamePasswordAuthenticationToken(user.getUsername(), null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
                    identityToken.setDetails(user);
                    identityToken.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(identityToken);
                }
            } catch (JwtException e) {
                // do nothing: continue to OAuth2
                log.info("failed to parse token");
            }
        } else {
            log.info("no token");
        }

        filterChain.doFilter(request, response);
    }

    @Deprecated
    private String getBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }
}
