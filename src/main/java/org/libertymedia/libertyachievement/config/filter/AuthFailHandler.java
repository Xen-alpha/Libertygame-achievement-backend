package org.libertymedia.libertyachievement.config.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFailHandler implements AuthenticationFailureHandler {
    private final Logger logger = LoggerFactory.getLogger(AuthFailHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.debug("OAuth2 failure: {}", exception.getMessage());
        // 쿠키에 토큰 설정
        ResponseCookie deletedCookie = ResponseCookie
                .from("AccessTOKEN", "")
                .path("/") //
                .httpOnly(true)
                .secure(true)
                .maxAge( 0L) // 180일
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deletedCookie.toString());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{ \"error\": \"OAuth2 login failed: " + exception.getMessage() + "\" }");
    }
}
