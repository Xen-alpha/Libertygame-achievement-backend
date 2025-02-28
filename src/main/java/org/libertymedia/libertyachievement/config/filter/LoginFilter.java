package org.libertymedia.libertyachievement.config.filter;


import org.libertymedia.libertyachievement.user.UserRepository;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.libertymedia.libertyachievement.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserInfo user = (UserInfo) authResult.getPrincipal();
        userRepository.findByUsername(user.getUsername()).ifPresent(userRepository::save);

        String jwtToken = JwtUtil.generateToken(user.getIdx(), user.getUsername());

        ResponseCookie cookie = ResponseCookie
                .from("ATOKEN", jwtToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofHours(1L))
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    }
}
