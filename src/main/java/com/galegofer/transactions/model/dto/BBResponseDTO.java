package com.galegofer.transactions.model.dto;

import lombok.Value;

/**
 * Data Transfer Object that represents the mapped object from Openbank to
 * Basebase one.
 * 
 * @author Damian
 */
public @Value class BBResponseDTO {
    private String id;
    private String accountId;
    private String counterpartyAccount;
    private String counterpartyName;
    private String counterPartyLogoPath;
    private Double instructedAmount;
    private String instructedCurrency;
    private Double transactionAmount;
    private String transactionCurrency;
    private String transactionType;
    private String description;
}
