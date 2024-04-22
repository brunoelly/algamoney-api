package com.algaworks.algamoneyapi.repository;

import com.algaworks.algamoneyapi.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Page<Transaction> findAllByDescriptionContainingIgnoreCaseOrDueDateBetween(Pageable pageable, String description, LocalDate stardDate, LocalDate endDate);
}
