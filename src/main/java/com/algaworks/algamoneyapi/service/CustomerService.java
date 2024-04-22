package com.algaworks.algamoneyapi.service;

import com.algaworks.algamoneyapi.exception.DuplicateResourceException;
import com.algaworks.algamoneyapi.exception.ResourceNotFoundException;
import com.algaworks.algamoneyapi.model.Customer;
import com.algaworks.algamoneyapi.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        customerRepository.findByCustomerNameContaining(customer.getCustomerName())
                .ifPresent(existingCustomer -> {
                    throw new DuplicateResourceException("Customer with email: " + customer.getCustomerEmail() + " already exists");
                });
        return customerRepository.save(customer);
    }
    public Customer updateCustomer(String customerId, Customer updatedCustomer) {
        return customerRepository.findById(customerId)
                .map(customer -> {
                    updatedCustomer.setCustomerId(customerId);
                    updatedCustomer.setIsActive(updatedCustomer.getIsActive() != null ? updatedCustomer.getIsActive() : customer.getIsActive());
                    return customerRepository.save(updatedCustomer);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + customerId));
    }

    public void updateIsActive(String customerId, Boolean isActive) {
        Customer retrievedCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + customerId));
        retrievedCustomer.setIsActive(isActive);
        customerRepository.save(retrievedCustomer);
    }
}
