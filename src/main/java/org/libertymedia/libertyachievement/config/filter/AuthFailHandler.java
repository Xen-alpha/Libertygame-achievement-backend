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
        logger.error("OAuth2 failure: ", exception);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"OAuth2 login failed: " + exception.getMessage() + "\"}");
    }
}
