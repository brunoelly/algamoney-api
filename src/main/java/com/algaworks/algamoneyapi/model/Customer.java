package com.algaworks.algamoneyapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customer")
@Data
@Builder
public class Customer {

    @Id
    private String customerId;
    @NotNull(message = "Name is required")
    private String customerName;
    @NotNull
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotNull
    private Address customerAddress;
    private Boolean isActive;

    @JsonIgnore
    @Transient
    public boolean isActive() {
        return this.isActive;
    }
}
