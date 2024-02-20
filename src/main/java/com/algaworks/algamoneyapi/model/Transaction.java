package com.algaworks.algamoneyapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private String customerId;
    @NotNull
    private String categoryId;
    @NotNull
    private TransactionType transactionType;
    private String description;
    @NotNull
    private Double amount;
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyy")
    private LocalDate dueDate;
    @NotNull
    @JsonFormat(pattern = "dd/MM/yyy")
    private LocalDate paymentDate;
}
