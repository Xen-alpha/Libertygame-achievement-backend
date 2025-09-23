package org.libertymedia.libertyachievement.config.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libertymedia.libertyachievement.user.model.UserInfo;
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
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = resolveJwt(request);
            // Get user info and make authentication class
            if (token != null) {
                try {
                    UserInfo user = JwtUtil.getUser(token);
                    if (user != null) {
                        UsernamePasswordAuthenticationToken identityToken = new UsernamePasswordAuthenticationToken(user.getUsername(), null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
                        identityToken.setDetails(user);
                        SecurityContextHolder.getContext().setAuthentication(identityToken);
                        log.info("user {} authenticated", user.getIdx());
                    } else {
                        log.info("no user exists / cookie is expired");
                    }
                } catch (JwtException e) {
                    // do nothing: continue to OAuth2
                    log.info("failed to parse token");
                }
            } else {
                log.info("no token, access to anonymous user");
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveJwt(HttpServletRequest req) {
        // HttpOnly 쿠키에서 가져옴
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("AccessTOKEN".equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }

    private String getLibertyUserName(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("libertyUserName".equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }

}
