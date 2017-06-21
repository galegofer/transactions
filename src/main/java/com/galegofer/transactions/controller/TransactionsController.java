package com.galegofer.transactions.controller;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galegofer.transactions.model.dto.BBResponseDTO;
import com.galegofer.transactions.service.TransactionsService;

import io.swagger.annotations.ApiOperation;

/**
 * Controller class that holds all the endpoints to query the OpenBank API and
 * process later the mapping.
 * 
 * @author Damian
 */
@Controller
@RequestMapping(value = "/v1/current-accounts")
public class TransactionsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionsController.class);

    @Autowired
    private TransactionsService transactionsService;

    /**
     * Retrieves all the existing transactions with a HTTP 200.
     * 
     * @return a list with all the transactions, converted using the required
     *         mapping.
     */
    @ApiOperation(value = "Retrieves all the existing transactions")
    @RequestMapping(value = "/transactions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> getTransactionsList() {
        LOGGER.info("Start getTransactionsList");

        List<BBResponseDTO> transactionsList = transactionsService.getTransactionsList();

        LOGGER.debug("getTransactionsList returned: '{}'", transactionsList);

        return new ResponseEntity<>(transactionsList, HttpStatus.OK);
    }

    /**
     * Retrieves all existing transactions associated with the given transaction
     * id and with a HTTP 200 in case of success, otherwise 401 if the
     * transactionType parameters is not supplied or is blank.
     * 
     * @param transactionType
     *            the transaction type to be used to filter with.
     * 
     * @return a list with all the filtered transactions by transactionType,
     *         converted using the required mapping.
     */
    @ApiOperation(value = "Retrieves all existing transactions associated with the given transaction id")
    @RequestMapping(value = "/{transactionType}/transactions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> getTransactionsPerType(
            @PathVariable("transactionType") String transactionType) {
        Validate.notBlank(transactionType, "Transaction type must be not null or empty");

        LOGGER.info("Start getTransactionsPerType with transaction type: '{}'", transactionType);

        List<BBResponseDTO> transactionsPerType = transactionsService.getTransactionsPerType(transactionType);

        LOGGER.debug("getTransactionsPerType returned: '{}'", transactionsPerType);

        return new ResponseEntity<>(transactionsPerType, HttpStatus.OK);
    }

    /**
     * Retrieves all the accumulative amounts for the specified transactionType
     * and with a HTTP 200 in case of success, otherwise 401 if the
     * transactionType parameters is not supplied or is blank.
     * 
     * @param transactionType
     *            the transaction type to be used to filter with.
     * 
     * @return a accumulation of the amount filtered by the given transaction
     *         type.
     */
    @ApiOperation(value = "Retrieves all the accumulative amounts for the specified transactionType")
    @RequestMapping(value = "/{transactionType}/totalAmmount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Object> getTransactionTotalsPerType(
            @PathVariable("transactionType") String transactionType) {
        Validate.notBlank(transactionType, "Transaction type must be not null or empty");

        LOGGER.info("Start getTransactionTotalsPerType with transaction type: '{}'", transactionType);

        Double transactionTotalsPerType = transactionsService.getTransactionTotalsPerType(transactionType);

        LOGGER.debug("getTransactionTotalsPerType returned: '{}'", transactionTotalsPerType);

        return new ResponseEntity<>(transactionTotalsPerType, HttpStatus.OK);
    }
}
