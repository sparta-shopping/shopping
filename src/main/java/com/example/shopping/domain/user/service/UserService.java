package com.example.shopping.domain.user.service;


import com.example.shopping.common.exception.ErrorCode;
import com.example.shopping.common.exception.InvalidRequestException;
import com.example.shopping.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.shopping.domain.user.dto.response.GetUserResponseDto;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.shopping.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public GetUserResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));
        return new GetUserResponseDto(user.getId(),user.getEmail());
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto) {
        validateNewPassword(changePasswordRequestDto);

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

        if (!passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(INVALID_PASSWORD.getStatus(), INVALID_PASSWORD.getMessage());
        }
        if (passwordEncoder.matches(changePasswordRequestDto.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(PASSWORD_SAME_AS_OLD.getStatus(), PASSWORD_SAME_AS_OLD.getMessage());
        }

        user.changePassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
    }

    private static void validateNewPassword(ChangePasswordRequestDto changePasswordRequestDto) {
        if (changePasswordRequestDto.getNewPassword().length() < 8 ||
                !changePasswordRequestDto.getNewPassword().matches(".*\\d.*") ||
                !changePasswordRequestDto.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }
}
