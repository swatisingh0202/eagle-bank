package com.eaglebank.feature.user.web;

import com.eaglebank.feature.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/" + UUID.randomUUID()))
                .andExpect(status().isOk());
    }
}