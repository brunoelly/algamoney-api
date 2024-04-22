package com.algaworks.algamoneyapi.dto;

import com.algaworks.algamoneyapi.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionStatisticsByDay {

    private TransactionType transactionType;

    private LocalDate dia;

    private BigDecimal total;

}
