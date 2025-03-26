package com.example.shopping.domain.user.controller;

import com.example.shopping.common.dto.AuthUser;
import com.example.shopping.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.shopping.domain.user.dto.response.GetUserResponseDto;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<GetUserResponseDto> getUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PatchMapping("/password")
    public void changePassword(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        userService.changePassword(authUser.getId(), changePasswordRequestDto);
    }



}
