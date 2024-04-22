package com.algaworks.algamoneyapi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactInfo {

    private String contactInfoId;
    private String name;
    private String email;
    private String phoneNumber;
    private Customer customer;
}
