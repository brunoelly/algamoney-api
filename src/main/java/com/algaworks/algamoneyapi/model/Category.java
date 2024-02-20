package com.algaworks.algamoneyapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    @Id
    private String categoryId;

    @NotNull
    @Size(min = 3, max = 50)
    private String categoryName;

}
