package com.example.shopping.domain.user.service;

import com.example.shopping.common.exception.InvalidRequestException;
import com.example.shopping.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.shopping.domain.user.dto.response.GetUserResponseDto;
import com.example.shopping.domain.user.entity.User;
import com.example.shopping.domain.user.repository.UserRepository;
import com.example.shopping.domain.user.role.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("asdf@gmail.com",
                "Asdf1234",
                "홍길동",
                "대한민국",
                UserRole.ROLE_ADMIN);
        userRepository.save(testUser);
        when(userRepository.findById(testUser.getId())).thenReturn(java.util.Optional.of(testUser));

    }


    @Test
    void getUser성공() {
        // given
        Long userId = testUser.getId();

        // when
        GetUserResponseDto response = userService.getUser(userId);

        // then
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals(testUser.getEmail(), response.getEmail());
    }

}