package com.v01.techgear_server.dto;

import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class UserDTO implements UserDetails{
    
    private Long id;
    private String username;
    private String password;
    private String email;
    private List<ReviewsDTO> reviews;
    private UserPhoneNoDTO phoneNumbers;
    private boolean active;
    private UserAddressDTO addresses;
    private List<String> passwordHistory;
    private Set<RolesDTO> roles;
    private ConfirmationTokensDTO confirmationTokensDTO;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getRoleType().name()))
            .collect(Collectors.toList());
    }
    @Override
    public String getUsername() {
        return this.username;
    }
    
}
