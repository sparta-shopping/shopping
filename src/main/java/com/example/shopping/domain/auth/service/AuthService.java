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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.shopping.common.exception.ErrorCode.INVALID_TOKEN;
import static com.example.shopping.common.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public SignupResponseDto signUp(@Valid SignupRequestDto signupRequestDto) {

        if (userRepository.existsUserByEmail(signupRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
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
                () -> new IllegalArgumentException("가입되지 않은 유저입니다."));

        if (!passwordEncoder.matches(signinRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user.getId(),user.getEmail(),user.getRole(),user.getName(),user.getAddress());

        String refreshToken = jwtUtil.createRefreshToken(user.getId(),accessToken);

        return new SigninResponseDto(accessToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken getRefreshToken(String accessToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByAccessToken(accessToken).orElseThrow(
                ()-> new ResponseStatusException(INVALID_TOKEN.getStatus(),INVALID_TOKEN.getMessage()));
        return refreshToken;
    }

    @Transactional
    public void removeRefreshToken(String accessToken) {
        refreshTokenRepository.findByAccessToken(accessToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public String reCreateAccessToken(String originAccessToken, RefreshToken refreshToken) {
        Long userId = refreshToken.getId();
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new ResponseStatusException(USER_NOT_FOUND.getStatus(),USER_NOT_FOUND.getMessage()));
        String newAccessToken = jwtUtil.createAccessToken(user.getId(),user.getEmail(),user.getRole(),user.getName(),user.getAddress());

        removeRefreshToken(originAccessToken);
        refreshTokenRepository.save(new RefreshToken(userId,newAccessToken,refreshToken.getRefreshToken()));
        return newAccessToken;

    }

}
