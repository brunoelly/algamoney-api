package com.algaworks.algamoneyapi.security;

import com.algaworks.algamoneyapi.model.ApplicationUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class ApplicationUserImpl extends User {
    private static final long serialVersionUID = 1L;

    private ApplicationUser applicationUser;

    public ApplicationUserImpl(ApplicationUser applicationUser, Collection<? extends GrantedAuthority> authorities) {
        super(applicationUser.getUserEmail(), applicationUser.getUserPassword(), authorities);
        this.applicationUser = applicationUser;
    }

    public ApplicationUser getApplicationUser() {
        return applicationUser;
    }
}