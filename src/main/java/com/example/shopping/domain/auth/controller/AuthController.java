package com.example.shopping.domain.auth.controller;

import com.example.shopping.common.util.JwtUtil;
import com.example.shopping.domain.auth.dto.request.RefreshTokenRequestDto;
import com.example.shopping.domain.auth.dto.request.SigninRequestDto;
import com.example.shopping.domain.auth.dto.request.SignupRequestDto;
import com.example.shopping.domain.auth.dto.response.SignupResponseDto;
import com.example.shopping.domain.auth.dto.response.SigninResponseDto;
import com.example.shopping.domain.auth.entity.RefreshToken;
import com.example.shopping.domain.auth.repository.RefreshTokenRepository;
import com.example.shopping.domain.auth.service.AuthService;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;


    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signUp(
            @Valid @RequestBody SignupRequestDto signupRequestDto
    ) {
        SignupResponseDto signupResponseDto = authService.signUp(signupRequestDto);

        String token = signupResponseDto.getToken();

        return ResponseEntity.ok()
                .header("Authorization",token)
                .body(signupResponseDto);
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponseDto> signIn(
            @Valid @RequestBody SigninRequestDto signinRequestDto
    ) {
        SigninResponseDto signinResponseDto = authService.signIn(signinRequestDto);

        String bearerToken = signinResponseDto.getToken();

        return ResponseEntity.ok()
                .header("Authorization",bearerToken)
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(
            @RequestBody RefreshTokenRequestDto request) {

        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());

        return ResponseEntity.ok(newAccessToken);
    }

}
