package com.algaworks.algamoneyapi.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Document(collection = "user")
@Data
@Builder
@Setter
@Getter
public class ApplicationUser {

    @Id
    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;
    private List<Permissions> permissions;
    public Collection<GrantedAuthority> grantedAuthorities;
}