package com.example.shopping.domain.auth.service;

import com.example.shopping.common.util.JwtUtil;
import com.example.shopping.domain.auth.dto.request.SignupRequestDto;
import com.example.shopping.domain.auth.dto.response.SignupResponseDto;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponseDto signUp(@Valid SignupRequestDto signupRequestDto) {

        if (userRepository.existsUserByEmail(signupRequestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        UserRole role = UserRole.of(signupRequestDto.getRole());

        User newUser = new User(
                signupRequestDto.getName(),
                signupRequestDto.getEmail(),
                encodedPassword,
                signupRequestDto.getAddress(),
                role
        );

        User savedUser = userRepository.save(newUser);

        String token = jwtUtil.createToken(savedUser.getId(),savedUser.getEmail(), role,savedUser.getName(),savedUser.getAddress());

        return new SignupResponseDto(token);
    }


}
