package com.algaworks.algamoneyapi.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Document(collection = "categories")
@Builder
@Data
public class Category {

    @Id
    private String categoryId;

    @NotNull
    @Size(min = 3, max = 20)
    private String categoryName;

}
