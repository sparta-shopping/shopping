package com.example.shopping.common.util;


import com.example.shopping.common.exception.ServerException;
import com.example.shopping.domain.auth.entity.RefreshToken;
import com.example.shopping.domain.auth.repository.RefreshTokenRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;



import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisUtil redisUtil;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60 * 60 * 1000L; //60분
    private static final long REFRESH_TOKEN_TIME = 60 * 60 * 7 *24 *1000L; //1주일

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(Long userId, String email, UserRole role, String name, String address) {
        Date date = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .claim("name", name)
                .claim("address", address)
                .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                .setIssuedAt(date) //발급일
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String createRefreshToken(Long userId, String accessToken) {
        Date date = new Date();

        Claims claims = Jwts.claims().setSubject(Long.toString(userId));

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime()+ REFRESH_TOKEN_TIME))
                .signWith(key)
                .compact();

        refreshTokenRepository.save(new RefreshToken(userId,accessToken, refreshToken));

        // Redis에 저장
        redisUtil.save("refreshToken:" + userId, refreshToken, REFRESH_TOKEN_TIME, TimeUnit.MILLISECONDS);
        return refreshToken;
    }


    public String substringToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }
        throw new ServerException("유효하지않은 토큰입니다.");
    }

    public Claims extractClaims(String token) {
        System.out.println(token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
