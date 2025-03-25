package com.example.shopping.domain.auth.service;

import com.example.shopping.common.util.JwtUtil;
import com.example.shopping.domain.auth.dto.request.SigninRequestDto;
import com.example.shopping.domain.auth.dto.request.SignupRequestDto;
import com.example.shopping.domain.auth.dto.response.SigninResponseDto;
import com.example.shopping.domain.auth.dto.response.SignupResponseDto;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

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

        String token = jwtUtil.createToken(savedUser.getId(),savedUser.getEmail(), role,savedUser.getName(),savedUser.getAddress());

        return new SignupResponseDto(token);
    }

    @Transactional(readOnly = true)
    public SigninResponseDto signIn(@Valid SigninRequestDto signinRequestDto) {
        User user = userRepository.findByEmail(signinRequestDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("가입되지 않은 유저입니다."));

        if (!passwordEncoder.matches(signinRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        String token = jwtUtil.createToken(user.getId(),user.getEmail(),user.getRole(),user.getName(),user.getAddress());

        return new SigninResponseDto(token);
    }


}
