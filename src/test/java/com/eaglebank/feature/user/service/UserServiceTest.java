package com.eaglebank.feature.user.service;

import com.eaglebank.feature.auth.repository.domain.Identity;
import com.eaglebank.feature.auth.service.IdentityService;
import com.eaglebank.feature.user.repository.UserRepository;
import com.eaglebank.feature.user.repository.domain.User;
import com.eaglebank.feature.user.web.model.CreateUserRequest;
import com.eaglebank.feature.user.web.model.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.eaglebank.feature.common.TestIds.USER_ID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private IdentityService identityService;
    @InjectMocks
    private UserService userService;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private User user;

    @BeforeEach
    void setUp() {
        createUserRequest = CreateUserRequest.builder()
                .name("test")
                .password("test")
                .email("test")
                .phone("test")
                .build();
        updateUserRequest = UpdateUserRequest.builder()
                .name("test")
                .phone("test")
                .build();
        user = User.builder()
                .name("test")
                .email("test")
                .phone("test")
                .build();
    }

    @Test
    void createUser() {
        when(userRepository.createUser(user)).thenReturn(USER_ID);
        var response = userService.createUser(createUserRequest);

        verify(userRepository).createUser(user);
        verify(identityService).createIdentity(USER_ID, Identity.builder().password("test").email("test").build());
        assert response.getUserId().equals(USER_ID);
        assert response.getName().equals("test");
        assert response.getEmail().equals("test");
        assert response.getPhone().equals("test");
    }

    @Test
    void getUser() {
        when(userRepository.getUser(USER_ID)).thenReturn(user);
        var response = userService.getUser(USER_ID);
        verify(userRepository).getUser(USER_ID);
        assert response.getUserId().equals(USER_ID);
        assert response.getName().equals("test");
        assert response.getEmail().equals("test");
        assert response.getPhone().equals("test");
    }

    @Test
    void updateUser() {
        user = User.builder()
                .name("test")
                .phone("test")
                .build();
        userService.updateUser(USER_ID, updateUserRequest);
        verify(userRepository).updateUser(USER_ID, user);
    }
}