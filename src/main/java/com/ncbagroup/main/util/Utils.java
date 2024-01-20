package com.ncbagroup.main.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncbagroup.main.entity.AccountEntity;
import com.ncbagroup.main.model.request.Account;
import com.ncbagroup.main.model.request.Card;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    public Utils(ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    private String generateRandomCharacters(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomChars = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            randomChars.append(characters.charAt(index));
        }

        return randomChars.toString();
    }

    public String timestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return now.format(formatter);
    }

    public String UniqueIdGenerator() {
        String randomChars = generateRandomCharacters(3).toUpperCase(); // Generate 3 random characters
        return randomChars + timestamp();
    }


    public Object setJsonStringToObject(String content, Class<?> object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(content, object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public Map<String, String> buildValidationErrorResponse(Account account) {
        Map<String, String> errors = new HashMap<>();

        // Perform custom validation and populate the errors map accordingly
        if (account.getIban() == null || account.getIban().isEmpty()) {
            errors.put("iban", "iban cannot be blank");
        }

        if (account.getClientId() == null || account.getClientId().isEmpty()) {
            errors.put("clientid", "client id cannot be blank");
        }

        if (account.getBicSwift() == null || account.getBicSwift().isEmpty()) {
            errors.put("bicswift", "bic swift id cannot be blank");
        }

        // Add more custom validation rules as needed

        return errors;
    }

    public Map<String, String> buildCardValidationErrorResponse(Card card) {
        Map<String, String> errors = new HashMap<>();

        // Perform custom validation and populate the errors map accordingly
        if (card.getCardType() == null || card.getCardType().isEmpty()) {
            errors.put("cardType", "card type cannot be blank");
        }

        if (card.getAlias() == null || card.getAlias().isEmpty()) {
            errors.put("alias", "alias cannot be blank");
        }

        if (card.getAccountId() == null ) {
            errors.put("accountId", "account id cannot be blank");
        }

        // Add more custom validation rules as needed

        return errors;
    }

    public String convertMapToJson(Map<String, String> map) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }

    public List<Account> convertEntityListToDTOList(List<AccountEntity> entityList) {
        return entityList.stream()
                .map(accountEntity -> modelMapper.map(accountEntity, Account.class))
                .collect(Collectors.toList());
    }

    public String convertListToJson(List<Account> accountList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(accountList);
    }


    public <D, E> D convertEntityToDto(E entity, Class<D> dtoClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.convertValue(entity, dtoClass);
    }

    public <D, E> E convertDtoToEntity(D dto, Class<E> entityClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.convertValue(dto, entityClass);
    }


    public void logResponseEntity(ResponseEntity<?> responseEntity, String action, Boolean error) throws JsonProcessingException {

            if(!error){
                logger.info("---------------------------- "+action+" start ---------------------------------------");
                logger.info("Response Status: {}", responseEntity.getStatusCode());
                logger.info("Response Body: {}", convertObjectToJson(responseEntity.getBody()));
                logger.info("---------------------------- "+action+" end -----------------------------------------");
            }else{
                logger.error("---------------------------- "+action+" start ---------------------------------------");
                logger.error("Response Status: {}", responseEntity.getStatusCode());
                logger.error("Response Body: {}", responseEntity.getBody());
                logger.error("---------------------------- "+action+" end -----------------------------------------");
            }

    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.writeValueAsString(object);
    }

}
