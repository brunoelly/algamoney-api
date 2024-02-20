package com.algaworks.algamoneyapi.resource;

import com.algaworks.algamoneyapi.event.ResourceCreatedEvent;
import com.algaworks.algamoneyapi.model.Transaction;
import com.algaworks.algamoneyapi.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
public class TransactionResource {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    public List<Transaction> listTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> findTransactionById(@PathVariable String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        return transaction.isPresent() ? ResponseEntity.ok(transaction.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction  transaction, HttpServletResponse response) {
        Transaction savedTransaction = transactionRepository.save(transaction);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, savedTransaction.getTransactionId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }
}
