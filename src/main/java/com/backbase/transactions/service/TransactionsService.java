package com.backbase.transactions.service;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.backbase.transactions.model.dto.BBResponseDTO;
import com.backbase.transactions.model.dto.OpenBankResponseDTO;
import com.jayway.jsonpath.JsonPath;

/**
 * 
 * Service class that provides all the method necessaries to perform the REST
 * call to the webservice.
 * 
 * @author Damian
 */
@Service
public class TransactionsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionsService.class);
    private RestTemplate restTemplate;
    private final Function<Object, BBResponseDTO> typeConverter;
    private String bankUrl;

    public TransactionsService() {
        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/application.properties"));
            bankUrl = properties.getProperty("open_bank_url");
        } catch (IOException e) {
            bankUrl = "http://localhost:8080";
            LOGGER.error("Error while loocking for 'open_bank_url' variable, using default one to: ",
                    "http://localhost:8080");
        }

        restTemplate = new RestTemplate();

        // Converts the response from OpenBank to Backbase format.
        typeConverter = transactionDTO -> {
            BBResponseDTO bbResponseDTO = new BBResponseDTO();
            bbResponseDTO.setId(JsonPath.read(transactionDTO, "$.id"));
            bbResponseDTO.setAccountId(JsonPath.read(transactionDTO, "$.this_account.id"));
            bbResponseDTO.setCounterpartyAccount(JsonPath.read(transactionDTO, "$.other_account.number"));
            bbResponseDTO.setCounterpartyName(JsonPath.read(transactionDTO, "$.other_account.holder.name"));
            bbResponseDTO.setCounterPartyLogoPath(JsonPath.read(transactionDTO, "$.other_account.metadata.image_URL"));
            bbResponseDTO.setInstructedAmount(Double.valueOf(JsonPath.read(transactionDTO, "$.details.value.amount")));
            bbResponseDTO.setInstructedCurrency(JsonPath.read(transactionDTO, "$.details.value.currency"));
            bbResponseDTO.setTransactionAmount(Double.valueOf(JsonPath.read(transactionDTO, "$.details.value.amount")));
            bbResponseDTO.setTransactionCurrency(JsonPath.read(transactionDTO, "$.details.value.currency"));
            bbResponseDTO.setTransactionType(JsonPath.read(transactionDTO, "$.details.type"));
            bbResponseDTO.setDescription(JsonPath.read(transactionDTO, "$.details.description"));

            LOGGER.debug("Converting OpenBank response: '{}' to Backbase one: '{}'", transactionDTO, bbResponseDTO);

            return bbResponseDTO;
        };
    }

    /**
     * Retrieves all existing transactions and converts them according to the
     * mapping.
     * 
     * @return a list with all the transactions, converted using the required
     *         mapping or empty if no one was found.
     */
    public List<BBResponseDTO> getTransactionsList() {
        ResponseEntity<OpenBankResponseDTO> response = restTemplate.getForEntity(bankUrl, OpenBankResponseDTO.class);

        HttpStatus status = response.getStatusCode();

        if (status.is2xxSuccessful()) {
            OpenBankResponseDTO body = response.getBody();

            LOGGER.debug("Succesfuly got a response from OpenBank Server: '{}'", body);

            return Optional.ofNullable(body).map(OpenBankResponseDTO::getTransactions).get().stream().map(typeConverter)
                    .collect(toList());
        } else {
            LOGGER.warn("Can't get transactions from service at url: '{}', response status was: '{}'", bankUrl, status);
        }

        return Collections.emptyList();
    }

    /**
     * Retrieves all existing transactions associated with the given transaction
     * id and converted using the required mapping.
     * 
     * @param transactionType
     *            the transaction type to be used to filter with.
     * 
     * @return a list with all the filtered transactions by transactionType,
     *         converted using the required mapping or empty if no one was
     *         found.
     */
    public List<BBResponseDTO> getTransactionsPerType(String transactionType) {
        ResponseEntity<OpenBankResponseDTO> response = restTemplate.getForEntity(bankUrl, OpenBankResponseDTO.class);

        HttpStatus status = response.getStatusCode();

        if (status.is2xxSuccessful()) {
            OpenBankResponseDTO body = response.getBody();

            LOGGER.debug("Succesfuly got a response from OpenBank Server: '{}'", body);

            return Optional.ofNullable(body).map(OpenBankResponseDTO::getTransactions).get().stream()
                    .filter(transactionDTO -> {
                        return transactionType.equals(JsonPath.read(transactionDTO, "$.details.type"));
                    }).map(typeConverter).collect(toList());
        } else {
            LOGGER.warn("Can't get transactions per type from service at url: '{}', response status was: '{}'", bankUrl,
                    status);
        }

        return Collections.emptyList();
    }

    /**
     * Retrieves all the accumulative amounts for the specified transactionType.
     * 
     * @param transactionType
     *            the transaction type to be used to filter with.
     * 
     * @return a accumulation of the amount filtered by the given transaction
     *         type.
     */
    public Double getTransactionTotalsPerType(String transactionType) {
        ResponseEntity<OpenBankResponseDTO> response = restTemplate.getForEntity(bankUrl, OpenBankResponseDTO.class);

        HttpStatus status = response.getStatusCode();

        if (status.is2xxSuccessful()) {
            OpenBankResponseDTO body = response.getBody();

            LOGGER.debug("Succesfuly got a response from OpenBank Server: '{}'", body);

            return Optional.ofNullable(body).map(OpenBankResponseDTO::getTransactions).get().stream()
                    .filter(transactionDTO -> {
                        return transactionType.equals(JsonPath.read(transactionDTO, "$.details.type"));
                    }).map(typeConverter).map(BBResponseDTO::getTransactionAmount)
                    .reduce(0d, (previous, next) -> previous + next);
        } else {
            LOGGER.warn("Can't get transactions totals per type from service at url: '{}', response status was: '{}'",
                    bankUrl, status);
        }

        return 0d;
    }
}
