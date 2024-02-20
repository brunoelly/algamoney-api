package com.algaworks.algamoneyapi.repository.impl;

import com.algaworks.algamoneyapi.model.Transaction;
import com.algaworks.algamoneyapi.repository.TransactionRepositoryCustom;
import com.algaworks.algamoneyapi.repository.filter.TransactionFilter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public TransactionRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Transaction> filter(TransactionFilter transactionFilter) {
        Query query = new Query();
        if (transactionFilter.getDescription() != null) {
            query.addCriteria(Criteria.where("description").is(transactionFilter.getDescription()));
        }
        if (transactionFilter.getDueDateFrom() != null) {
            query.addCriteria(Criteria.where("dueDate").gte(transactionFilter.getDueDateFrom()));
        }
        if (transactionFilter.getDueDateTill() != null) {
            query.addCriteria(Criteria.where("dueDate").lte(transactionFilter.getDueDateTill()));
        }
        return mongoTemplate.find(query, Transaction.class);
    }
}
