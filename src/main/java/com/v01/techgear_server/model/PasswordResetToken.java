package com.v01.techgear_server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    private LocalDateTime expiredDate;

    public boolean isExpired() {
        return expiredDate.isBefore(LocalDateTime.now());
    }
}