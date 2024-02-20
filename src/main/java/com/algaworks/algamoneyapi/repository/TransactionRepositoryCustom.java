package com.algaworks.algamoneyapi.repository;

import com.algaworks.algamoneyapi.model.Transaction;
import com.algaworks.algamoneyapi.repository.filter.TransactionFilter;

import java.util.List;

public interface TransactionRepositoryCustom {

    List<Transaction> filter(TransactionFilter transactionFilter);
}
