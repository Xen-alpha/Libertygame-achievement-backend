package org.libertymedia.libertyachievement.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.libertymedia.libertyachievement.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

@Deprecated(since="사용 가능성이 없어짐")
@RequiredArgsConstructor
public class LoginRoutingFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger logger = LoggerFactory.getLogger(LoginRoutingFilter.class);
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        logger.info("UsernamePasswordAuth Filter 작동");
        UsernamePasswordAuthenticationToken loginToken = null;
        String tokenStr = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("AccessTOKEN")) {
                tokenStr = cookie.getValue();
                break;
            }
        }
        UserInfo userInfo = JwtUtil.getUser(tokenStr);
        loginToken = new UsernamePasswordAuthenticationToken(userInfo, null,  List.of(new SimpleGrantedAuthority("ROLE_"+userInfo.getRole())));
        return authenticationManager.authenticate(loginToken);
    }

    @Deprecated
    private String getBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
        UserInfo userInfo = (UserInfo) auth.getPrincipal();
        String jwt = JwtUtil.generateToken(userInfo.getUserIdx(), userInfo.getUsername(), userInfo.getEmail(), userInfo.getRole(), userInfo.getNotBlocked());
        logger.info("Success in UsernamePasswordFilter Auth.");
        ResponseCookie cookie = ResponseCookie.from("AccessTOKEN", jwt)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(86400 * 180L) // 1시간(3600초) 유효시간
                .build();
        res.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{ \"result\": \"로그인 성공\" }");
    }
}
