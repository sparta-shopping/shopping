package com.example.shopping.domain.auth.service;

import com.example.shopping.common.util.JwtUtil;
import com.example.shopping.domain.auth.dto.request.SigninRequestDto;
import com.example.shopping.domain.auth.dto.request.SignupRequestDto;
import com.example.shopping.domain.auth.dto.response.SigninResponseDto;
import com.example.shopping.domain.auth.dto.response.SignupResponseDto;
import com.example.shopping.domain.auth.entity.RefreshToken;
import com.example.shopping.domain.auth.repository.RefreshTokenRepository;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.shopping.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public SignupResponseDto signUp(@Valid SignupRequestDto signupRequestDto) {

        if (userRepository.existsUserByEmail(signupRequestDto.getEmail())) {
            throw new ResponseStatusException(USER_EMAIL_DUPLICATION.getStatus(), USER_EMAIL_DUPLICATION.getMessage());
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        UserRole role = UserRole.of(signupRequestDto.getRole());

        User newUser = new User(
                signupRequestDto.getEmail(),
                encodedPassword,
                signupRequestDto.getName(),
                signupRequestDto.getAddress(),
                role
        );

        User savedUser = userRepository.save(newUser);

        String accessToken = jwtUtil.createAccessToken(savedUser.getId(),savedUser.getEmail(), role,savedUser.getName(),savedUser.getAddress());

        String refreshToken = jwtUtil.createRefreshToken(savedUser.getId(),accessToken);

        return new SignupResponseDto(accessToken);
    }

    @Transactional(readOnly = true)
    public SigninResponseDto signIn(@Valid SigninRequestDto signinRequestDto) {
        User user = userRepository.findByEmail(signinRequestDto.getEmail()).orElseThrow(
                () -> new ResponseStatusException(USER_NOT_FOUND.getStatus(),USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(signinRequestDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(INVALID_PASSWORD.getStatus(), INVALID_PASSWORD.getMessage());
        }

        String accessToken = jwtUtil.createAccessToken(user.getId(),user.getEmail(),user.getRole(),user.getName(),user.getAddress());

        String refreshToken = jwtUtil.createRefreshToken(user.getId(),accessToken);

        return new SigninResponseDto(accessToken);
    }


    public String refreshAccessToken(String refreshTokenValue) {
        // Refresh Token 조회 및 검증
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                .orElseThrow(() -> new ResponseStatusException(TOKEN_NOT_FOUND.getStatus(), TOKEN_NOT_FOUND.getMessage()));

        if (!jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
            throw new ResponseStatusException(EXPIRED_TOKEN.getStatus(), EXPIRED_TOKEN.getMessage());
        }

        // 사용자 정보 조회
        Long userId = refreshToken.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(),USER_NOT_FOUND.getMessage()));

        // 새로운 Access Token 생성
        String newAccessToken = jwtUtil.createAccessToken(
                user.getId(), user.getEmail(), user.getRole(), user.getName(), user.getAddress());

        // Refresh Token 업데이트
        refreshTokenRepository.save(new RefreshToken(userId, newAccessToken, refreshToken.getRefreshToken()));

        return newAccessToken;
    }


}
