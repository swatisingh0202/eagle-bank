package com.eaglebank.feature.user.web;

import com.eaglebank.feature.auth.JwtProvider;
import com.eaglebank.feature.common.exception.ResourceNotFoundException;
import com.eaglebank.feature.user.service.UserService;
import com.eaglebank.feature.user.web.model.CreateUserRequest;
import com.eaglebank.feature.user.web.model.UpdateUserRequest;
import com.eaglebank.feature.user.web.model.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.eaglebank.feature.common.TestIds.USER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    public static final String USERS_PATH = "/v1/users/";
    public static final String AUTHORIZATION = "Authorization";
    @MockitoBean
    private JwtProvider jwtProvider;
    @MockitoBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        createUserRequest = CreateUserRequest.builder()
                .name("Swati")
                .email("swati@gmail.com")
                .phone("07676767678")
                .password("Teest1234")
                .build();
        updateUserRequest = UpdateUserRequest.builder()
                .name("B")
                .phone("07676767676")
                .build();
        userResponse = UserResponse.builder()
                .build();
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);
        mockMvc.perform(post("/v1/users")
                        .content(objectMapper.writeValueAsString(createUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Given a valid userId and authentication, when GET /v1/users/{userId}, then return 200 and user details")
    void getUser() throws Exception {
        // Given
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(USER_ID);
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(USERS_PATH + USER_ID)
                        .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given a different userId in token, when GET /v1/users/{userId}, then return 403 Forbidden")
    void getUser_forbidden() throws Exception {
        // Given
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(java.util.UUID.randomUUID());
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(USERS_PATH + USER_ID)
                        .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Given a non-existent userId, when GET /v1/users/{userId}, then return 404 Not Found")
    void getUser_notFound() throws Exception {
        // Given
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(USER_ID);
        when(userService.getUser(USER_ID)).thenThrow(new ResourceNotFoundException("User not found"));
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(USERS_PATH + USER_ID)
                        .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser() throws Exception {
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(USER_ID);
        mockMvc.perform(patch(USERS_PATH + USER_ID)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
        verify(userService).updateUser(USER_ID, updateUserRequest);
    }

    @Test
    @DisplayName("Given a different userId in token, when PATCH /v1/users/{userId}, then return 403 Forbidden")
    void updateUser_forbidden() throws Exception {
        // Given
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(java.util.UUID.randomUUID());
        // When & Then
        mockMvc.perform(patch(USERS_PATH + USER_ID)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Given a non-existent userId, when PATCH /v1/users/{userId}, then return 404 Not Found")
    void updateUser_notFound() throws Exception {
        // Given
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(USER_ID);
        doThrow(new ResourceNotFoundException("User not found")).when(userService).updateUser(USER_ID, updateUserRequest);
        // When & Then
        mockMvc.perform(patch(USERS_PATH + USER_ID)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}