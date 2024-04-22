package com.algaworks.algamoneyapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "customer")
@Data
@Builder
public class Customer {

    @Id
    private String customerId;
    @NotNull(message = "Name is required")
    private String customerName;
    private Address customerAddress;
    private Boolean isActive;
    private List<ContactInfo> contactInfoList;

    @JsonIgnore
    @Transient
    public boolean isActive() {
        return this.isActive;
    }
}
