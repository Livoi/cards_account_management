package com.ncbagroup.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncbagroup.main.controller.AccountController;
import com.ncbagroup.main.controller.CardController;
import com.ncbagroup.main.model.request.Account;
import com.ncbagroup.main.model.request.Card;
import com.ncbagroup.main.repository.CardRepository;
import com.ncbagroup.main.service.AccountServiceInterface;
import com.ncbagroup.main.service.CardServiceInterface;
import com.ncbagroup.main.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CreditAccountManagementApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private CardRepository cardRepository;

	@Mock
	private CardServiceInterface cardServiceInterface;

	@Mock
	private AccountServiceInterface accountServiceInterface;

	@Mock
	private Utils utils;

	@InjectMocks
	private CardController cardController;

	@InjectMocks
	private AccountController accountController;

	@Autowired
	private CreditAccountManagementApplication creditAccountManagementApplication;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(cardController, accountController).build();
	}

	// ... (CardControllerTest and AccountControllerTest methods)
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
	@Test
	void createCard_ValidCard_ReturnsOk() throws Exception {
		// Given
		Card validCard = new Card();
		validCard.setAccountId(1L);
		validCard.setAlias("Unit Test");
		validCard.setCardType("Virtual");

		when(accountServiceInterface.GetAccount(validCard.getAccountId())).thenReturn(new Account());
		when(cardServiceInterface.CreateNewCard(any(Card.class))).thenReturn(ResponseEntity.ok().build());

		// When
		mockMvc.perform(post("/api/v1/create-card")
						.content(asJsonString(validCard))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
	}

	@Test
	void getCard_ValidCardId_ReturnsOk() throws Exception {
		// Given
		String validCardId = "validCardId";
		when(cardServiceInterface.GetCard(validCardId)).thenReturn(ResponseEntity.ok().build());

		// When
		mockMvc.perform(get("/api/v1/{cardId}", validCardId))
				.andExpect(status().isOk());
	}

	@Test
	void updateCard_ValidCardIdAndUpdatedCard_ReturnsOk() throws Exception {
		// Given
		Long validCardId = 1L;
		Card updatedCard = new Card();
		updatedCard.setAlias("updatedAlias");
		updatedCard.setCardType("updatedCardType");

		when(cardServiceInterface.UpdateCard(eq(validCardId), any(Card.class))).thenReturn(ResponseEntity.ok().build());

		// When
		mockMvc.perform(put("/api/v1/card/{cardId}", validCardId)
						.content(asJsonString(updatedCard))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void deleteCard_ValidCardId_ReturnsNoContent() throws Exception {
		// Given
		Long validCardId = 1L;
		when(cardRepository.existsById(validCardId)).thenReturn(true);

		// When
		mockMvc.perform(delete("/api/v1/card/{cardId}", validCardId))
				.andExpect(status().isNoContent());
	}

	@Test
	void getCardsByAccountId_ValidAccountId_ReturnsOk() throws Exception {
		// Given
		Long validAccountId = 1L;
		when(cardServiceInterface.GetAllCardsAssociatedWithAccount(validAccountId)).thenReturn(ResponseEntity.ok().build());

		// When
		mockMvc.perform(get("/api/v1/account/{accountId}", validAccountId))
				.andExpect(status().isOk());
	}


	@Test
	void contextLoads() {
		// Check if the application context loads successfully
		assertThat(creditAccountManagementApplication).isNotNull();
	}

	// Utility method to convert objects to JSON string
	private String asJsonString(Object object) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(object);
	}
}

