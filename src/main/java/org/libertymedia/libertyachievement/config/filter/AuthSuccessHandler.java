package org.libertymedia.libertyachievement.config.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.UserRepository;

import org.libertymedia.libertyachievement.user.model.LibertyOAuth2User;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Value("${HOST_DOMAIN}")
    private String host;

    private final Logger logger = LoggerFactory.getLogger(AuthSuccessHandler.class);
    private final UserRepository userRepository;

    // OAuth2가 성공했을 때의 행동: OAuth2 과정 도중 아직 도전과제 사용하지 않은 사용자면 서비스의 loadUser 과정에서 DB에 UserInfo 정보가 생성되므로 그냥 여기서 Refresh Token 저장하고 원래 사이트로 리다이렉트
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        LibertyOAuth2User authUser = (LibertyOAuth2User) authentication.getPrincipal();
        UserInfo user = userRepository.findByUsername(authUser.getName()).orElseThrow();
        String refreshToken = JwtUtil.generateRefreshToken(user.getUserIdx(), user.getUsername(), user.getEmail(), user.getRole(), user.getNotBlocked());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        // make 'AccessTOKEN' Cookie and give it to client
        String token = JwtUtil.generateToken(user.getUserIdx(), user.getUsername(), user.getEmail(), user.getRole(), user.getNotBlocked());
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        // 쿠키에 토큰 설정
        ResponseCookie cookie2 = ResponseCookie
                .from("RefreshTOKEN", token)
                .path("/") // '/user'
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .maxAge( 86400L * 180L) // 180일
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie2.toString());
        // JSESSIONID 제거
        ResponseCookie jsessionIdCookie = ResponseCookie
                .from("JSESSIONID", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(0) // 즉시 만료
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jsessionIdCookie.toString());
        logger.info("Successfully authenticated user: {}", user.getUsername());

        response.sendRedirect("/api/user");
    }
}
