package org.libertymedia.libertyachievement.config.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.libertymedia.libertyachievement.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Objects;

@Deprecated
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

        if (authToken == null) {
            return null;
        } else {
            userInfo = JwtUtil.getUser(authToken);
            if (userInfo == null) {
                return null;
            }
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userInfo.getUsername(), null, Objects.requireNonNull(userInfo).getAuthorities());
            token.setAuthenticated(true);
            return authenticationManager.authenticate(token);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.sendRedirect("/");
    }
}
