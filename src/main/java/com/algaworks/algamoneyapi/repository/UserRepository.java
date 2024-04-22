package com.algaworks.algamoneyapi.repository;

import com.algaworks.algamoneyapi.model.ApplicationUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<ApplicationUser, String> {

    public Optional<ApplicationUser> findByUserEmail(String email);

    @Query(value = "{ 'permissions.description' : ?0 }")
    public List<ApplicationUser> findByPermissions(String permissionDescription);
}
