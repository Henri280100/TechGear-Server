package com.v01.techgear_server.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.v01.techgear_server.enums.AuthProvider;
import com.v01.techgear_server.enums.UserGenders;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "username")
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private UserGenders genders;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name="image_id")
  private Image userAvatar;

  @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
  // @JoinColumn(name = "phone_id")
  private UserPhoneNo phoneNumbers;

  @Column(name = "email", unique = true)
  private String email;

  @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  // @JoinColumn(name = "address_id")
  private UserAddress addresses;

  @Enumerated(EnumType.STRING)
  private AuthProvider provider; // for storing data in gg or fb

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
  private ConfirmationTokens confirmationTokens;

  public User(String username, String password,
      UserPhoneNo phoneNumbers, UserAddress addresses, String email) {

    super();

    this.username = username;
    this.password = password;
    this.phoneNumbers = phoneNumbers;
    this.addresses = addresses;

    this.email = email;
  }

  @Column(name = "active")
  private boolean active;

  @OneToMany(mappedBy = "user")
  private List<Review> reviews;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleType().name()))
        .collect(Collectors.toList());
  }

  public boolean isAccountNonExpired() {
    return true;
  }

  public boolean isAccountNonLocked() {
    return true;
  }

  public boolean isCredentialsNonExpired() {
    return true;
  }

  public boolean isEnabled() {
    return this.active && this.confirmationTokens != null && this.confirmationTokens.getConfirmedAt() != null;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

}
