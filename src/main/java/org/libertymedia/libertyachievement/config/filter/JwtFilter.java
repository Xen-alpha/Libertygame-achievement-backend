package org.libertymedia.libertyachievement.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.libertymedia.libertyachievement.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JwtFilter 실행됐다.");
        Cookie[] cookies = request.getCookies();

        String jwtToken = null;
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("ATOKEN")) {
                    jwtToken = cookie.getValue();
                }
            }
        }

        if(jwtToken != null) {
            UserInfo user = JwtUtil.getUser(jwtToken);

            if(user != null) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(user);

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

        }

        filterChain.doFilter(request, response);
    }
}
