package com.algaworks.algamoneyapi.repository.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TransactionFilter {

    private String description;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate dueDateFrom;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate dueDateTill;

}
