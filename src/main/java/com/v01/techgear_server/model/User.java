package com.v01.techgear_server.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.v01.techgear_server.enums.AuthProvider;
import com.v01.techgear_server.enums.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long userId;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "email", unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  private AuthProvider provider; // for storing data in gg or fb

  @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
  private AccountDetails accountDetails;

  @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
  @JsonIgnore
  private ConfirmationTokens confirmationTokens;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @Column(name = "active")
  private boolean active;

  @Column(name = "login_attempts")
  private int loginAttempts;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_status")
  private UserStatus userStatus;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles.stream()
        .flatMap(role -> Stream.concat(
            Stream.of(new SimpleGrantedAuthority(role.getRoleType().name())),
            role.getRoleType().getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))))
        .collect(Collectors.toSet());
  }

  @CreationTimestamp
  @Column(updatable = false, name = "createdDate")
  private LocalDateTime createdDate;

  @UpdateTimestamp
  @Column(name = "updatedDate")
  private LocalDateTime updatedDate;

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return active &&
        confirmationTokens != null &&
        confirmationTokens.getConfirmedAt() != null;
  }

  public int getLoginAttempts() {
    return loginAttempts;
  }

  public void incrementLoginAttempts() {
    this.loginAttempts++;
  }

  public void resetLoginAttempts() {
    this.loginAttempts = 0;
  }
}
