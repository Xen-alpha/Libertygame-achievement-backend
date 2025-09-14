package org.libertymedia.libertyachievement.config.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFailHandler implements AuthenticationFailureHandler {
    private final Logger logger = LoggerFactory.getLogger(AuthFailHandler.class);

    @Value("${OAUTH_CLIENT_ID}")
    private String OAUTH_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.libertygame.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //redirect to OAuth 2.0 again
        logger.info("Authentication Failed: {}", exception.getMessage());
        response.sendRedirect("/rest.php/oauth2/authorize?response_type=code&client_id="+OAUTH_CLIENT_ID + "&redirect_uri="+ redirectUri); // 웹 앱 내부 오류의 경우 이 때문에 허가 페이지 연결하는 무한 루프를 돈다.
    }
}
