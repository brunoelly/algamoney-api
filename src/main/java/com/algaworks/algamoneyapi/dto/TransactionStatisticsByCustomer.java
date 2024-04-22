package com.algaworks.algamoneyapi.dto;

import com.algaworks.algamoneyapi.model.Customer;
import com.algaworks.algamoneyapi.model.TransactionType;

import java.math.BigDecimal;

public class TransactionStatisticsByCustomer {

    private TransactionType transactionType;

    private Customer pessoa;

    private BigDecimal total;

}