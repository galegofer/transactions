package com.backbase.transactions.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ObjectUtils;

import com.backbase.transactions.model.dto.BBResponseDTO;
import com.backbase.transactions.service.TransactionsService;

public class TransactionsControllerTest {

    @InjectMocks
    private TransactionsController underTest;

    @Mock
    private TransactionsService transactionsService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        initMocks(this);

        this.mockMvc = standaloneSetup(underTest).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    public void testGetTransactionsListNoEmptyResponse() throws Exception {
        List<BBResponseDTO> response = new ArrayList<>();
        response.add(new BBResponseDTO());

        when(transactionsService.getTransactionsList()).thenReturn(response);

        MvcResult responseBody = this.mockMvc
                .perform(get("/v1/current-accounts/transactions").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();

        assertEquals(HttpStatus.OK.value(), responseBody.getResponse().getStatus());
        assertTrue(!ObjectUtils.isEmpty(responseBody));
        verify(transactionsService).getTransactionsList();
    }

    @Test
    public void testGetTransactionsListEmptyResponse() throws Exception {
        List<BBResponseDTO> response = new ArrayList<>();

        when(transactionsService.getTransactionsList()).thenReturn(response);

        MvcResult responseBody = this.mockMvc
                .perform(get("/v1/current-accounts/transactions").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();

        assertEquals(HttpStatus.OK.value(), responseBody.getResponse().getStatus());
        assertTrue(!ObjectUtils.isEmpty(responseBody));
        verify(transactionsService).getTransactionsList();
    }

    @Test
    public void testGetTransactionsPerTypeNoEmptyTransactionTypeNoEmptyResponse() throws Exception {
        List<BBResponseDTO> response = new ArrayList<>();
        response.add(new BBResponseDTO());

        when(transactionsService.getTransactionsPerType(Mockito.anyString())).thenReturn(response);

        MvcResult responseBody = this.mockMvc
                .perform(get("/v1/current-accounts/testType/transactions").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();

        assertEquals(HttpStatus.OK.value(), responseBody.getResponse().getStatus());
        assertTrue(!ObjectUtils.isEmpty(responseBody));
        verify(transactionsService).getTransactionsPerType(Mockito.anyString());
    }

    @Test
    public void testGetTransactionsPerTypeNoEmptyTransactionTypeEmptyResponse() throws Exception {
        List<BBResponseDTO> response = new ArrayList<>();

        when(transactionsService.getTransactionsPerType(Mockito.anyString())).thenReturn(response);

        MvcResult responseBody = this.mockMvc
                .perform(get("/v1/current-accounts/testType/transactions").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();

        assertEquals(HttpStatus.OK.value(), responseBody.getResponse().getStatus());
        assertTrue(!ObjectUtils.isEmpty(responseBody));
        verify(transactionsService).getTransactionsPerType(Mockito.anyString());
    }

    @Test
    public void testGetTransactionsPerTypeEmptyTransactionTypeNoEmptyResponseWithExceptionHandler() throws Exception {
        MvcResult responseBody = this.mockMvc
                .perform(get("/v1/current-accounts/  /transactions").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.getResponse().getStatus());
        assertTrue(!ObjectUtils.isEmpty(responseBody));
    }

    @Test
    public void testGetTransactionTotalsPerTypeNoEmptyTransactionTypeNoEmptyResponse() throws Exception {
        Double response = 0d;

        when(transactionsService.getTransactionTotalsPerType(Mockito.anyString())).thenReturn(response);

        MvcResult responseBody = this.mockMvc
                .perform(get("/v1/current-accounts/testType/totalAmmount").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();

        assertEquals(HttpStatus.OK.value(), responseBody.getResponse().getStatus());
        assertTrue(!ObjectUtils.isEmpty(responseBody));
        verify(transactionsService).getTransactionTotalsPerType(Mockito.anyString());
    }

    @Test
    public void testTransactionTotalsPerTypeEmptyTransactionTypeNoEmptyResponse() throws Exception {
        this.mockMvc.perform(get("/v1/current-accounts/  /totalAmmount").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn();
    }

    @Test
    public void testGetTransactionTotalsPerTypeNoEmptyTransactionTypeEmptyResponse() throws Exception {
        Double response = null;

        when(transactionsService.getTransactionTotalsPerType(Mockito.anyString())).thenReturn(response);

        MvcResult responseBody = this.mockMvc
                .perform(get("/v1/current-accounts/testType/totalAmmount").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful()).andReturn();

        assertEquals(HttpStatus.OK.value(), responseBody.getResponse().getStatus());
        assertTrue(!ObjectUtils.isEmpty(responseBody));
        verify(transactionsService).getTransactionTotalsPerType(Mockito.anyString());
    }
}
