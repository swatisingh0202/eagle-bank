package com.eaglebank.feature.user.service;

import com.eaglebank.feature.account.repository.BankAccountRepository;
import com.eaglebank.feature.auth.service.IdentityService;
import com.eaglebank.feature.common.exception.ConflictException;
import com.eaglebank.feature.common.exception.ResourceNotFoundException;
import com.eaglebank.feature.user.repository.UserRepository;
import com.eaglebank.feature.user.repository.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private IdentityService identityService;
    @Mock
    private BankAccountRepository bankAccountRepository;
    @InjectMocks
    private UserService userService;
    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = User.builder().userId(userId).name("Test User").build();
    }

    @Test
    @DisplayName("Given a user with no bank accounts, when deleteUser, then user and identity are deleted")
    void deleteUser_success() {
        // Given
        when(userRepository.getUser(userId)).thenReturn(user);
        when(bankAccountRepository.getAccountsByUserId(userId)).thenReturn(Collections.emptyList());
        // When
        userService.deleteUser(userId);
        // Then
        verify(userRepository).deleteUser(userId);
        verify(identityService).deleteIdentity(userId);
    }

    @Test
    @DisplayName("Given a user with bank accounts, when deleteUser, then throw ConflictException")
    void deleteUser_withBankAccounts_conflict() {
        // Given
        when(userRepository.getUser(userId)).thenReturn(user);
        when(bankAccountRepository.getAccountsByUserId(userId)).thenReturn(List.of(mock(Object.class)));
        // When & Then
        assertThrows(ConflictException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteUser(userId);
        verify(identityService, never()).deleteIdentity(userId);
    }

    @Test
    @DisplayName("Given a non-existent user, when deleteUser, then throw ResourceNotFoundException")
    void deleteUser_userNotFound() {
        // Given
        when(userRepository.getUser(userId)).thenReturn(null);
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteUser(userId);
        verify(identityService, never()).deleteIdentity(userId);
    }
}

