package com.eaglebank.feature.account;

import com.eaglebank.feature.account.service.BankAccountService;
import com.eaglebank.feature.account.web.model.AccountType;
import com.eaglebank.feature.account.web.model.BankAccountResponse;
import com.eaglebank.feature.account.web.model.CreateBankAccountRequest;
import com.eaglebank.feature.account.web.model.UpdateBankAccountRequest;
import com.eaglebank.feature.auth.JwtProvider;
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

import java.util.List;

import static com.eaglebank.feature.common.TestIds.ACCOUNT_ID;
import static com.eaglebank.feature.common.TestIds.USER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BankAccountController.class)
class BankAccountControllerTest {
    @MockitoBean
    private JwtProvider jwtProvider;
    @MockitoBean
    private BankAccountService bankAccountService;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private CreateBankAccountRequest createBankAccountRequest;
    private UpdateBankAccountRequest updateBankAccountRequest;
    private BankAccountResponse bankAccountResponse;

    @BeforeEach
    void setUp() {
        createBankAccountRequest = CreateBankAccountRequest.builder()
                .name("my account")
                .accountType(AccountType.PERSONAL)
                .build();
        updateBankAccountRequest = UpdateBankAccountRequest.builder()
                .name("updated account")
                .accountType(AccountType.PERSONAL)
                .build();
        bankAccountResponse = BankAccountResponse.builder().build();
    }

    @Test
    @DisplayName("Given valid data, when POST /v1/accounts/user/{userId}, then a new bank account is created and 201 is returned")
    void createAccount() throws Exception {
        // Given
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(USER_ID);
        when(bankAccountService.createAccount(eq(USER_ID), any(CreateBankAccountRequest.class))).thenReturn(bankAccountResponse);
        // When & Then
        mockMvc.perform(post("/v1/accounts/user/" + USER_ID)
                        .content(objectMapper.writeValueAsString(createBankAccountRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
        verify(bankAccountService).createAccount(eq(USER_ID), any(CreateBankAccountRequest.class));
    }

    @Test
    @DisplayName("Given missing required data, when POST /v1/accounts/user/{userId}, then return 400 Bad Request")
    void createAccount_missingData() throws Exception {
        // Given
        String token = "123456";
        CreateBankAccountRequest invalidRequest = CreateBankAccountRequest.builder().name(null).accountType(null).build();
        when(jwtProvider.getUserId(token)).thenReturn(USER_ID);
        // When & Then
        mockMvc.perform(post("/v1/accounts/user/" + USER_ID)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given a valid accountId and Authorization header, when GET /v1/accounts/{accountId}, then return 200 OK")
    void getAccount() throws Exception {
        // Given
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(USER_ID);
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/accounts/" + ACCOUNT_ID)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given valid update data, when PATCH /v1/accounts/{accountId}, then return 200 OK")
    void updateAccount() throws Exception {
        // Given/When/Then
        mockMvc.perform(patch("/v1/accounts/" + ACCOUNT_ID)
                        .content(objectMapper.writeValueAsString(updateBankAccountRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bankAccountService).updateAccount(eq(ACCOUNT_ID), any(UpdateBankAccountRequest.class));
    }

    @Test
    @DisplayName("Given a valid accountId, when DELETE /v1/accounts/{accountId}, then the account is deleted and 200 OK is returned")
    void deleteAccount() throws Exception {
        // Given/When/Then
        mockMvc.perform(delete("/v1/accounts/" + ACCOUNT_ID))
                .andExpect(status().isOk());
        verify(bankAccountService).deleteAccount(ACCOUNT_ID);
    }

    @Test
    @DisplayName("Given a valid token, when GET /v1/accounts, then return 200 OK and accounts for user")
    void getAccountsForUser() throws Exception {
        // Given
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(USER_ID);
        when(bankAccountService.getAccountsByUserId(USER_ID)).thenReturn(List.of(bankAccountResponse));
        // When & Then
        mockMvc.perform(get("/v1/accounts")
                        .header("Authorization", token))
                .andExpect(status().isOk());
        verify(bankAccountService).getAccountsByUserId(USER_ID);
    }

}