package com.algaworks.algamoneyapi.resource;

import com.algaworks.algamoneyapi.event.ResourceCreatedEvent;
import com.algaworks.algamoneyapi.exception.InactiveCustomerException;
import com.algaworks.algamoneyapi.model.Transaction;
import com.algaworks.algamoneyapi.repository.TransactionRepository;
import com.algaworks.algamoneyapi.repository.filter.TransactionFilter;
import com.algaworks.algamoneyapi.service.TransactionService;
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

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private TransactionService transactionService;
    @Autowired private ApplicationEventPublisher publisher;

    @GetMapping
    public List<Transaction> transactionsFilter(TransactionFilter transactionFilter) {
        return transactionRepository.findAll();
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> findTransactionById(@PathVariable String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        return transaction.isPresent() ? ResponseEntity.ok(transaction.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction, HttpServletResponse response) throws InactiveCustomerException {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, createdTransaction.getTransactionId()));
        return ResponseEntity.ok(createdTransaction);
    }

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable String transactionId) {
        transactionRepository.deleteById(transactionId);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String transactionId, @RequestBody Transaction transaction) {
        Transaction updatedTransaction = transactionService.updateTransaction(transactionId, transaction);
        return ResponseEntity.ok(updatedTransaction);
    }

}
