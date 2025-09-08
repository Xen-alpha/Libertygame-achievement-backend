package org.libertymedia.libertyachievement.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    // private final AuthenticationManager authenticationManager;
    private final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        UserInfo user = (UserInfo) auth.getPrincipal();
        String accessToken = user.getPassword();
        logger.info("{}({})님에게 액세스 토큰 전달",user.getIdx(), user.getEmail());
        ResponseCookie cookie = ResponseCookie.from("Authorization", "bearer " + accessToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(86400) // 24시간 유효시간
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.setContentType("application/json");

        response.getWriter().write("{ success : true }");
    }
}
