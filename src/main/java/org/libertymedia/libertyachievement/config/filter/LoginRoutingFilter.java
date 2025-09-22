package org.libertymedia.libertyachievement.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.libertymedia.libertyachievement.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class LoginRoutingFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(LoginRoutingFilter.class);

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        logger.info("누군가 로그인을 시도함");
        // Cookie를 먼저 찾는다.
        Cookie[] cookies = request.getCookies();
        String authToken = null;
        UserInfo userInfo = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("AccessToken")) {
                    authToken = cookie.getValue();
                    break;
                }
            }
        }
        try {
            if (authToken == null) {
                response.sendRedirect("/api/login");
                return null;
            } else {
                userInfo = JwtUtil.getUser(authToken);
                if (userInfo == null) {
                    response.sendRedirect("/api/login");
                    return null;
                }
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userInfo, authToken, Objects.requireNonNull(userInfo).getAuthorities());
                token.setAuthenticated(true);
                return authenticationManager.authenticate(token);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Authentication failed");
        }
    }
}
