package com.galegofer.transactions.model.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object that represents the response that comes form the
 * OpenBank API.
 * 
 * Generally a list of transactions with their corresponding attributes.
 * 
 * @author Damian
 */
public class OpenBankResponseDTO {

    @JsonProperty("transactions")
    private List<Object> transactions;

    public OpenBankResponseDTO() {
    }

    public List<Object> getTransactions() {
        if (ObjectUtils.isEmpty(transactions)) {
            this.transactions = new ArrayList<>();
        }

        return transactions;
    }

    public void setTransactions(List<Object> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OpenBankResponseDTO [transactions=").append(transactions).append("]");
        return builder.toString();
    }
}