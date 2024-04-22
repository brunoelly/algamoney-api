package com.algaworks.algamoneyapi.security;

import com.algaworks.algamoneyapi.model.ApplicationUser;
import com.algaworks.algamoneyapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<ApplicationUser> userOptional = userRepository.findByUserEmail(email);
        ApplicationUser applicationUser = userOptional.orElseThrow(() -> new UsernameNotFoundException("Username or password incorrect"));
        return new User(email, applicationUser.getUserPassword(), getPermissions(applicationUser));
    }

    private Collection<? extends GrantedAuthority> getPermissions(ApplicationUser applicationUser) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        applicationUser.getPermissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getDescription().toUpperCase())));
        return authorities;
    }

}