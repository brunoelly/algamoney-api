package com.algaworks.algamoneyapi.service;

import com.algaworks.algamoneyapi.exception.InactiveCustomerException;
import com.algaworks.algamoneyapi.exception.ResourceNotFoundException;
import com.algaworks.algamoneyapi.model.Category;
import com.algaworks.algamoneyapi.model.Customer;
import com.algaworks.algamoneyapi.model.Transaction;
import com.algaworks.algamoneyapi.repository.CategoryRepository;
import com.algaworks.algamoneyapi.repository.CustomerRepository;
import com.algaworks.algamoneyapi.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

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
}
