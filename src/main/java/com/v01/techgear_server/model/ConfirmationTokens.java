package com.v01.techgear_server.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name="confirmation_tokens")
public class ConfirmationTokens {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long confirmationTokensId;

    @Column(name="confirmToken")
    private String confirmToken;

    @Column(name="createdDate")
    private LocalDateTime createdDate;

    @Column(name="expiryDate")
    private LocalDateTime expiryDate;
    
    @Column(name="confirmedAt")
    private LocalDateTime confirmedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User users;
}
