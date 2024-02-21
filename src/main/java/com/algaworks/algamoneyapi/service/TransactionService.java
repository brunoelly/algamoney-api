package com.algaworks.algamoneyapi.service;

import com.algaworks.algamoneyapi.exception.InactiveCustomerException;
import com.algaworks.algamoneyapi.exception.ResourceNotFoundException;
import com.algaworks.algamoneyapi.model.Customer;
import com.algaworks.algamoneyapi.model.Transaction;
import com.algaworks.algamoneyapi.repository.CategoryRepository;
import com.algaworks.algamoneyapi.repository.CustomerRepository;
import com.algaworks.algamoneyapi.repository.TransactionRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired private MongoOperations mongoOperations;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private CategoryRepository categoryRepository;

    public Transaction createTransaction(Transaction transaction) throws InactiveCustomerException {
        Customer customer = customerRepository.findById(transaction.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + transaction.getCustomerId()));

        if (!customer.isActive()) {
            throw new InactiveCustomerException("Cannot create a transaction for an inactive customer with id " + customer.getCustomerId());
        }

        categoryRepository.findById(transaction.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + transaction.getCategoryId()));

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(String transactionId, Transaction updatedTransaction) {
        transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + transactionId));

        Document document = new Document();
        mongoOperations.getConverter().write(updatedTransaction, document);
        Update update = new Update();
        document.forEach(update::set);

        return mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(transactionId)),
                update,
                Transaction.class);
    }
}
