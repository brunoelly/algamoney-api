package com.algaworks.algamoneyapi.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Permissions {

    @Id
    private String permissionId;
    private String description;
    private String userCode;
    private String permissionCode;
}
