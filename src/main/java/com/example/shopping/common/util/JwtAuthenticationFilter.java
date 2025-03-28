package com.example.shopping.common.util;


import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.domain.auth.entity.RefreshToken;
import com.example.shopping.domain.auth.repository.RefreshTokenRepository;
import com.example.shopping.domain.auth.service.AuthService;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static com.example.shopping.common.exception.ErrorCode.INVALID_TOKEN;
import static com.example.shopping.common.exception.ErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException, ServletException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = jwtUtil.substringToken(authorizationHeader);
            try {
                Claims claims = jwtUtil.extractClaims(jwt);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthentication(claims);
                }
            } catch (ExpiredJwtException e) {
                log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
                handleExpiredToken(response, jwt);
            } catch (SecurityException | MalformedJwtException e) {
                log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않는 JWT 서명입니다.");
            } catch (UnsupportedJwtException e) {
                log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
            } catch (Exception e) {
                log.error("Internal server error", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void handleExpiredToken(HttpServletResponse response, String expiredToken) throws IOException {
        RefreshToken refreshToken = getRefreshToken(expiredToken);
        if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
            String newAccessToken = authService.reCreateAccessToken(refreshToken);
            response.setHeader("New-Access-Token", newAccessToken);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "RefreshToken이 만료되었습니다. 재로그인이 필요합니다.");
        }
    }


    private void setAuthentication(Claims claims) {
        Long userId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);
        UserRole userRole = UserRole.of(claims.get("role", String.class));

        AuthUser authUser = new AuthUser(userId, email, userRole);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }


    public RefreshToken getRefreshToken(String accessToken) {
        return refreshTokenRepository.findByAccessToken(accessToken).orElse(null);
    }




}