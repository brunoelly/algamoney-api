package com.algaworks.algamoneyapi.repository;

import com.algaworks.algamoneyapi.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByCustomerNameContaining(String nome, Pageable pageable);
}
