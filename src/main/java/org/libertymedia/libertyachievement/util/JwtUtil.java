package org.libertymedia.libertyachievement.util;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.UserRepository;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    private static String SECRET;

    @Value("${jwt.expired}")
    private int exp;

    private static Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private static int EXP;

    @PostConstruct
    public void init() {
        SECRET = secret;
        EXP = exp;
    }

    public static UserInfo getUser(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return UserInfo.builder()
                    .userIdx(claims.get("userIdx", Long.class))
                    .username(claims.get("username", String.class))
                    .role(claims.get("role", String.class))
                    .notBlocked(claims.get("notBlocked", Boolean.class))
                    .email(claims.get("userEmail", String.class))
                    .expiresAt(ZonedDateTime.ofInstant(Instant.ofEpochMilli(claims.get("exp", Long.class)), ZoneId.systemDefault()))
                    .build();

        } catch (ExpiredJwtException e) {
            logger.debug("토큰이 만료되었습니다!");
            return null;
        }
    }

    public static String generateToken(Long userIdx, String userName, String userEmail, String role, Boolean notBlocked) {
        Claims claims = Jwts.claims();

        claims.put("userEmail", userEmail);
        claims.put("userName", userName);
        claims.put("userIdx", userIdx);
        claims.put("role", role);
        claims.put("notBlocked", notBlocked);
        claims.put("exp", System.currentTimeMillis() + EXP);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
        return token;
    }

    public static String generateRefreshToken(Long userIdx, String userName, String userEmail, String role, Boolean notBlocked) {
        Claims claims = Jwts.claims();

        claims.put("userEmail", userEmail);
        claims.put("userName", userName);
        claims.put("userIdx", userIdx);
        claims.put("role", role);
        claims.put("notBlocked", notBlocked);
        claims.put("exp", System.currentTimeMillis() + EXP * 28L);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXP * 28L))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
        return token;
    }

    public static boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.debug("토큰이 만료되었습니다!");
            return false;
        } catch (MalformedJwtException e) {
            logger.debug("토큰이 잘못되었습니다!");
            return false;
        } catch (SecurityException e) {
            logger.debug("토큰 서명이 잘못되었습니다!");
            return false;
        }
        return true;
    }
}
