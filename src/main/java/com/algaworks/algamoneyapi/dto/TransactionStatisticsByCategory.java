package com.algaworks.algamoneyapi.dto;

import com.algaworks.algamoneyapi.model.Category;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionStatisticsByCategory {

    private Category category;

    private BigDecimal total;

}