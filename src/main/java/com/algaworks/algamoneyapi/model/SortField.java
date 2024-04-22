package com.algaworks.algamoneyapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortField {

    ID("transactionId"),
    DESCRIPTION("description"),
    DUEDATE("dueDate");

    private final String dataBaseFieldName;
}
