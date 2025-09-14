package org.libertymedia.libertyachievement.config.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.UserRepository;

import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.libertymedia.libertyachievement.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Value("${jwt.expired}")
    private int exp;

    private final Logger logger = LoggerFactory.getLogger(AuthSuccessHandler.class);
    private final UserRepository userRepository;
    // OAuth2가 성공했을 때의 행동: OAuth2 과정 도중 아직 도전과제 사용하지 않은 사용자면 DB에 UserInfo 정보가 생성되므로 그냥 여기서 Access Token 발급 후 Refresh Token 저장하고 리다이렉트
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        UserInfo user = (UserInfo) authentication.getPrincipal();
        String refreshToken = JwtUtil.generateRefreshToken(user.getUserIdx(), user.getUsername(), user.getEmail(), user.getRole(), user.getNotBlocked());
        user.setRefreshToken(refreshToken);
        // make 'ATOKEN' Cookie and give it to client
        String token = JwtUtil.generateToken(user.getUserIdx(), user.getUsername(), user.getEmail(), user.getRole(), user.getNotBlocked());
        user.setPassword(token); // 액세스 토큰 저장
        userRepository.save(user);
        ResponseCookie cookie = ResponseCookie.from("AccessTOKEN", token)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(exp) // 유효시간
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        logger.info("Successfully authenticated user: " + user.getUsername());
        response.sendRedirect("/user/success");

    }
}
