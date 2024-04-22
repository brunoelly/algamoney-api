package com.algaworks.algamoneyapi.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.algaworks.algamoneyapi.model.TransactionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionReport {

    private Long reportId;
    private String description;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String category;
    private String customer;

}