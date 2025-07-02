package com.eaglebank.feature.account;

import com.eaglebank.feature.account.service.BankAccountService;
import com.eaglebank.feature.account.web.model.AccountType;
import com.eaglebank.feature.account.web.model.BankAccountResponse;
import com.eaglebank.feature.account.web.model.CreateBankAccountRequest;
import com.eaglebank.feature.account.web.model.UpdateBankAccountRequest;
import com.eaglebank.feature.auth.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.*;
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
    void createAccount() throws Exception {
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(USER_ID);
        when(bankAccountService.createAccount(eq(USER_ID), any(CreateBankAccountRequest.class))).thenReturn(bankAccountResponse);
        mockMvc.perform(post("/v1/accounts/user/" + USER_ID)
                        .content(objectMapper.writeValueAsString(createBankAccountRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
        verify(bankAccountService).createAccount(eq(USER_ID), any(CreateBankAccountRequest.class));
    }

    @Test
    void getAccount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/accounts/" + ACCOUNT_ID))
                .andExpect(status().isOk());
    }

    @Test
    void updateAccount() throws Exception {
        mockMvc.perform(patch("/v1/accounts/" + ACCOUNT_ID)
                        .content(objectMapper.writeValueAsString(updateBankAccountRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bankAccountService).updateAccount(eq(ACCOUNT_ID), any(UpdateBankAccountRequest.class));
    }

    @Test
    void deleteAccount() throws Exception {
        mockMvc.perform(delete("/v1/accounts/" + ACCOUNT_ID))
                .andExpect(status().isOk());
        verify(bankAccountService).deleteAccount(ACCOUNT_ID);
    }

    @Test
    void getAccountsForUser() throws Exception {
        String token = "123456";
        when(jwtProvider.getUserId(token)).thenReturn(USER_ID);
        when(bankAccountService.getAccountsByUserId(USER_ID)).thenReturn(List.of(bankAccountResponse));
        mockMvc.perform(get("/v1/accounts")
                        .header("Authorization", token))
                .andExpect(status().isOk());
        verify(bankAccountService).getAccountsByUserId(USER_ID);
    }

}