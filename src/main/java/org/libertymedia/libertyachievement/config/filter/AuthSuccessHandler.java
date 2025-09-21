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

    @Value("${jwt.expired}")
    private int exp;

    @Value("${HOST_DOMAIN}")
    private String host;

    private final Logger logger = LoggerFactory.getLogger(AuthSuccessHandler.class);
    private final UserRepository userRepository;


    // OAuth2가 성공했을 때의 행동: OAuth2 과정 도중 아직 도전과제 사용하지 않은 사용자면 DB에 UserInfo 정보가 생성되므로 그냥 여기서 Access Token 발급 후 Refresh Token 저장하고 리다이렉트
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        LibertyOAuth2User user = (LibertyOAuth2User) authentication.getPrincipal();
        String refreshToken = JwtUtil.generateRefreshToken(user.getUser().getUserIdx(), user.getUser().getUsername(), user.getUser().getEmail(), user.getUser().getRole(), user.getUser().getNotBlocked());
        user.getUser().setRefreshToken(refreshToken);
        // make 'ATOKEN' Cookie and give it to client
        String token = JwtUtil.generateToken(user.getUser().getUserIdx(), user.getUser().getUsername(), user.getUser().getEmail(), user.getUser().getRole(), user.getUser().getNotBlocked());
        user.getUser().setPassword(token); // 액세스 토큰 저장
        user.getUser().setExpiresAt(ZonedDateTime.now().plusHours(12));
        userRepository.save(user.getUser());
        // 쿠키에 토큰 설정
        ResponseCookie cookie = ResponseCookie
                .from("AccessTOKEN", token)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(exp)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        // JSESSIONID 제거
        ResponseCookie jsessionIdCookie = ResponseCookie
                .from("JSESSIONID", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(0) // 즉시 만료
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jsessionIdCookie.toString());
        logger.info("Successfully authenticated user: {}", user.getUser().getUsername());
        String redirectUrl = request.getRequestURI();
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            try {
                redirectUrl = URLDecoder.decode(redirectUrl, StandardCharsets.UTF_8);
                logger.info("Redirecting to original URL: {}", redirectUrl);
            } catch (Exception e) {
                logger.error("Failed to decode state parameter: {}", redirectUrl, e);
            }
        } else {
            redirectUrl = host;
            logger.warn("No state parameter found, using default redirect: {}", redirectUrl);
        }
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
            logger.info("Invalidated existing session");
        }

        response.sendRedirect(redirectUrl);
    }
}
