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

    @PostConstruct
    public void init() {
        SECRET = secret;
    }

    public static UserInfo getUser(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return UserInfo.builder()
                .userIdx(claims.get("userIdx", Long.class))
                .username(claims.get("userName", String.class))
                .role(claims.get("role", String.class))
                .notBlocked(claims.get("notBlocked", Boolean.class))
                .email(claims.get("userEmail", String.class))
                .build();
    }

    public static String generateToken(Long userIdx, String userName, String userEmail, String role, Boolean notBlocked) {
        Claims claims = Jwts.claims();

        claims.put("userEmail", userEmail);
        claims.put("userName", userName);
        claims.put("userIdx", userIdx);
        claims.put("role", role);
        claims.put("notBlocked", notBlocked);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(ZonedDateTime.now().toInstant().toEpochMilli()))
                .setExpiration(new Date(ZonedDateTime.now().toInstant().toEpochMilli() + 180*86400*1000L))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
        return token;
    }

    @Deprecated
    public static String generateRefreshToken(Long userIdx, String userName, String userEmail, String role, Boolean notBlocked) {
        Claims claims = Jwts.claims();

        claims.put("userEmail", userEmail);
        claims.put("userName", userName);
        claims.put("userIdx", userIdx);
        claims.put("role", role);
        claims.put("notBlocked", notBlocked);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(ZonedDateTime.now().toInstant().toEpochMilli()))
                .setExpiration(new Date(ZonedDateTime.now().toInstant().toEpochMilli() + 86400*1000*28L))
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
            logger.info("토큰이 만료되었습니다!");
            return false;
        } catch (MalformedJwtException e) {
            logger.info("토큰이 잘못되었습니다!");
            return false;
        } catch (SecurityException e) {
            logger.info("토큰 서명이 잘못되었습니다!");
            return false;
        }
        return true;
    }
}
