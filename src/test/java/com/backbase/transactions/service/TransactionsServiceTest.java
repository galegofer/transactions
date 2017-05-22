package com.backbase.transactions.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.backbase.transactions.model.dto.BBResponseDTO;
import com.backbase.transactions.model.dto.OpenBankResponseDTO;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class TransactionsServiceTest {

    private JSONObject jsonBody;

    @InjectMocks
    private TransactionsService underTest;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        jsonBody = (JSONObject) new JSONParser(JSONParser.MODE_JSON_SIMPLE).parse(
                "{\"id\": \"dcb8138c-eb88-404a-981d-d4edff1086a6\",\r\n      \"this_account\": {\r\n        \"id\": \"savings-kids-john\",\r\n        \"holders\": [\r\n          {\r\n            \"name\": \"Savings - Kids John\",\r\n            \"is_alias\": false\r\n          }\r\n        ],\r\n        \"number\": \"832425-00304050\",\r\n        \"kind\": \"savings\",\r\n        \"IBAN\": null,\r\n        \"swift_bic\": null,\r\n        \"bank\": {\r\n          \"national_identifier\": \"rbs\",\r\n          \"name\": \"The Royal Bank of Scotland\"\r\n        }\r\n      },\r\n      \"other_account\": {\r\n        \"id\": \"c83f9a12-171e-4602-9a92-ae895c41b16b\",\r\n        \"holder\": {\r\n          \"name\": \"ALIAS_CBCDE5\",\r\n          \"is_alias\": true\r\n        },\r\n        \"number\": \"13677980653\",\r\n        \"kind\": \"CURRENT PLUS\",\r\n        \"IBAN\": \"BA12 1234 5123 4513 6779 8065 377\",\r\n        \"swift_bic\": null,\r\n        \"bank\": {\r\n          \"national_identifier\": null,\r\n          \"name\": \"The Bank of X\"\r\n        },\r\n        \"metadata\": {\r\n          \"public_alias\": null,\r\n          \"private_alias\": null,\r\n          \"more_info\": null,\r\n          \"URL\": null,\r\n          \"image_URL\": null,\r\n          \"open_corporates_URL\": null,\r\n          \"corporate_location\": null,\r\n          \"physical_location\": null\r\n        }\r\n      },\r\n      \"details\": {\r\n        \"type\": \"sandbox-payment\",\r\n        \"description\": \"Description abc\",\r\n        \"posted\": \"2016-10-09T20:01:53Z\",\r\n        \"completed\": \"2016-10-09T20:01:53Z\",\r\n        \"new_balance\": {\r\n          \"currency\": \"GBP\",\r\n          \"amount\": null\r\n        },\r\n        \"value\": {\r\n          \"currency\": \"GBP\",\r\n          \"amount\": \"10.00\"\r\n        }\r\n      },\r\n      \"metadata\": {\r\n        \"narrative\": null,\r\n        \"comments\": [],\r\n        \"tags\": [],\r\n        \"images\": [],\r\n        \"where\": null\r\n      }\r\n    }");
    }

    @Test
    public void testGetTransactionsListNoEmpty() {
        List<Object> list = new ArrayList<>();
        list.add(jsonBody);

        OpenBankResponseDTO openBankResponseDTO = new OpenBankResponseDTO();
        openBankResponseDTO.setTransactions(list);

        ResponseEntity<OpenBankResponseDTO> response = new ResponseEntity<>(openBankResponseDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(OpenBankResponseDTO.class)))
                .thenReturn(response);

        List<BBResponseDTO> bbTransactions = underTest.getTransactionsList();

        assertEquals(1, bbTransactions.stream().count());
        assertEquals(true, objectsEquals(bbTransactions.get(0), openBankResponseDTO.getTransactions().get(0)));
    }

    @Test
    public void testGetTransactionsListEmpty() {
        List<Object> list = new ArrayList<>();

        OpenBankResponseDTO openBankResponseDTO = new OpenBankResponseDTO();
        openBankResponseDTO.setTransactions(list);

        ResponseEntity<OpenBankResponseDTO> response = new ResponseEntity<>(openBankResponseDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(OpenBankResponseDTO.class)))
                .thenReturn(response);

        List<BBResponseDTO> bbTransactions = underTest.getTransactionsList();

        assertEquals(0, bbTransactions.stream().count());
    }

    @Test
    public void testGetTransactionsPerTypeNoEmpty() {
        String transactionType = "sandbox-payment";

        List<Object> list = new ArrayList<>();
        list.add(jsonBody);

        OpenBankResponseDTO openBankResponseDTO = new OpenBankResponseDTO();
        openBankResponseDTO.setTransactions(list);

        ResponseEntity<OpenBankResponseDTO> response = new ResponseEntity<>(openBankResponseDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(OpenBankResponseDTO.class)))
                .thenReturn(response);

        List<BBResponseDTO> bbTransactions = underTest.getTransactionsPerType(transactionType);

        assertEquals(1, bbTransactions.stream().count());
        assertEquals(true, objectsEquals(bbTransactions.get(0), openBankResponseDTO.getTransactions().get(0)));
    }

    @Test
    public void testGetTransactionsPerTypeEmpty() {
        String transactionType = "sandbox-payment";

        List<Object> list = new ArrayList<>();

        OpenBankResponseDTO openBankResponseDTO = new OpenBankResponseDTO();
        openBankResponseDTO.setTransactions(list);

        ResponseEntity<OpenBankResponseDTO> response = new ResponseEntity<>(openBankResponseDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(OpenBankResponseDTO.class)))
                .thenReturn(response);

        List<BBResponseDTO> bbTransactions = underTest.getTransactionsPerType(transactionType);

        assertEquals(0, bbTransactions.stream().count());
    }
    
    @Test
    public void testGetTransactionsPerTypeEmptyNoMatch() {
        String transactionType = "no-match";

        List<Object> list = new ArrayList<>();

        OpenBankResponseDTO openBankResponseDTO = new OpenBankResponseDTO();
        openBankResponseDTO.setTransactions(list);

        ResponseEntity<OpenBankResponseDTO> response = new ResponseEntity<>(openBankResponseDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(OpenBankResponseDTO.class)))
                .thenReturn(response);

        List<BBResponseDTO> bbTransactions = underTest.getTransactionsPerType(transactionType);

        assertEquals(0, bbTransactions.stream().count());
    }

    @Test
    public void testGetTransactionTotalsPerTypeNoEmpty() {
        String transactionType = "sandbox-payment";

        List<Object> list = new ArrayList<>();
        list.add(jsonBody);

        OpenBankResponseDTO openBankResponseDTO = new OpenBankResponseDTO();
        openBankResponseDTO.setTransactions(list);

        ResponseEntity<OpenBankResponseDTO> response = new ResponseEntity<>(openBankResponseDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(OpenBankResponseDTO.class)))
                .thenReturn(response);

        Double actualAmount = underTest.getTransactionTotalsPerType(transactionType);

        assertEquals(Double.valueOf(10d), actualAmount);
    }
    
    @Test
    public void testGetTransactionTotalsPerTypeNoEmptyNoMatch() {
        String transactionType = "no-match";

        List<Object> list = new ArrayList<>();
        list.add(jsonBody);

        OpenBankResponseDTO openBankResponseDTO = new OpenBankResponseDTO();
        openBankResponseDTO.setTransactions(list);

        ResponseEntity<OpenBankResponseDTO> response = new ResponseEntity<>(openBankResponseDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(OpenBankResponseDTO.class)))
                .thenReturn(response);

        Double actualAmount = underTest.getTransactionTotalsPerType(transactionType);

        assertEquals(Double.valueOf(0d), actualAmount);
    }

    @Test
    public void testGetTransactionTotalsPerTypeEmpty() {
        String transactionType = "sandbox-payment";

        List<Object> list = new ArrayList<>();

        OpenBankResponseDTO openBankResponseDTO = new OpenBankResponseDTO();
        openBankResponseDTO.setTransactions(list);

        ResponseEntity<OpenBankResponseDTO> response = new ResponseEntity<>(openBankResponseDTO, HttpStatus.OK);

        when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(OpenBankResponseDTO.class)))
                .thenReturn(response);

        Double actualAmount = underTest.getTransactionTotalsPerType(transactionType);

        assertEquals(Double.valueOf(0d), actualAmount);
    }

    private boolean objectsEquals(BBResponseDTO bbResponseDTO, Object object) {
        if (bbResponseDTO.getId().equals(JsonPath.read(object, "$.id"))
                && bbResponseDTO.getAccountId().equals(JsonPath.read(object, "$.this_account.id"))
                && bbResponseDTO.getCounterpartyAccount().equals(JsonPath.read(object, "$.other_account.number"))
                && bbResponseDTO.getCounterpartyName().equals(JsonPath.read(object, "$.other_account.holder.name"))
                && bbResponseDTO.getInstructedAmount()
                        .equals(Double.valueOf(JsonPath.read(object, "$.details.value.amount")))
                && bbResponseDTO.getInstructedCurrency().equals(JsonPath.read(object, "$.details.value.currency"))
                && bbResponseDTO.getTransactionType().equals(JsonPath.read(object, "$.details.type"))
                && bbResponseDTO.getDescription().equals(JsonPath.read(object, "$.details.description"))) {
            return true;
        }

        return false;
    }
}
