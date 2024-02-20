package com.algaworks.algamoneyapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "transaction")
@Data
@Builder
public class Transaction {

    @Id
    private String transactionId;
    private Customer customer;
    private Category category;
    private TransactionType transactionType;
    private String description;
    private Double amount;
    @JsonFormat(pattern = "dd/MM/yyy")
    private LocalDate dueDate;
    @JsonFormat(pattern = "dd/MM/yyy")
    private LocalDate paymentDate;
}
