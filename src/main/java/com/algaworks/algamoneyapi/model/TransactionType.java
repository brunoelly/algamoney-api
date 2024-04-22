package com.algaworks.algamoneyapi.model;

public enum TransactionType {

    REVENUE("Revenue"),
    INCOME("Income");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
