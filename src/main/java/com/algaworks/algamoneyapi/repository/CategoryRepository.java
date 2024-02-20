package com.algaworks.algamoneyapi.repository;

import com.algaworks.algamoneyapi.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {

}
