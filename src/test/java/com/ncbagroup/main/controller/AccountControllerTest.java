package com.ncbagroup.main.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncbagroup.main.model.request.Account;
import com.ncbagroup.main.service.AccountServiceInterface;
import com.ncbagroup.main.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountServiceInterface accountServiceInterface;

    @Mock
    private Utils utils;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void createAccount_ValidAccount_ReturnsOk() throws Exception {
        // Given
        Account validAccount = new Account();
        validAccount.setIban("123456789");
        validAccount.setBicSwift("ABCXYZ");
        validAccount.setClientId("123456");

        // Use doReturn for ResponseEntity with generic type
        doReturn(ResponseEntity.ok(validAccount)).when(accountServiceInterface).CreateAccount(any(Account.class));

        // When
        mockMvc.perform(post("/api/v1/create-account")
                        .content(asJsonString(validAccount))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value(validAccount.getIban()))
                .andExpect(jsonPath("$.bicSwift").value(validAccount.getBicSwift()));
    }

    @Test
    void getAccount_ValidAccountId_ReturnsOk() throws Exception {
        // Given
        Long validAccountId = 1L;
        Account validAccount = new Account();
        validAccount.setId(validAccountId);
        validAccount.setIban("123456789");
        validAccount.setBicSwift("ABCXYZ");

        when(accountServiceInterface.GetAccount(validAccountId)).thenReturn(validAccount);

        // When
        mockMvc.perform(get("/api/v1/accounts/{accountId}", validAccountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validAccountId))
                .andExpect(jsonPath("$.iban").value(validAccount.getIban()))
                .andExpect(jsonPath("$.bicSwift").value(validAccount.getBicSwift()));
    }

    // Utility method to convert objects to JSON string
    private String asJsonString(Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }
}
