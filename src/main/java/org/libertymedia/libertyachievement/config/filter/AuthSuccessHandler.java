package org.libertymedia.libertyachievement.config.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.libertymedia.libertyachievement.user.UserRepository;

import org.libertymedia.libertyachievement.user.model.LibertyOAuth2User;
import org.libertymedia.libertyachievement.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(AuthSuccessHandler.class);
    private final UserRepository userRepository;

    @Value("${HOST_OAUTH}")
    private String host;

    // OAuth2가 성공했을 때의 행동: OAuth2 과정 도중 아직 도전과제 사용하지 않은 사용자면 서비스의 loadUser 과정에서 DB에 UserInfo 정보가 생성되므로 컨트롤러에 정의된 API로 리다이렉트
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        LibertyOAuth2User authUser = (LibertyOAuth2User) authentication.getPrincipal();
        // make access token and give it to client
        String token = JwtUtil.generateToken(authUser.getUser().getUserIdx(), authUser.getUser().getUsername(), authUser.getUser().getEmail(), authUser.getUser().getRole(), authUser.getUser().getNotBlocked());
        // 쿠키에 토큰 설정
        ResponseCookie cookie2 = ResponseCookie
                .from("AccessTOKEN", token)
                .path("/") //
                .httpOnly(true)
                .secure(true)
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
        logger.info("Successfully authenticated user: {}", authUser.getUser().getUsername());
        // 리다이렉트 조정
        response.sendRedirect( "https://" + host + "/wiki/"+ URLEncoder.encode("리버티게임:도전_과제", StandardCharsets.UTF_8));
    }
}
