package com.galegofer.transactions.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * Data Transfer Object that represents the response that comes form the
 * OpenBank API.
 * 
 * Generally a list of transactions with their corresponding attributes.
 * 
 * @author Damian
 */
public @Value class OpenBankResponseDTO {

    @JsonProperty("transactions")
    private List<Object> transactions;
}