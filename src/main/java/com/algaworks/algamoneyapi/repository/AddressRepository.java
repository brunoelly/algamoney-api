package com.algaworks.algamoneyapi.repository;

import com.algaworks.algamoneyapi.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends MongoRepository<Address, String> {
}
