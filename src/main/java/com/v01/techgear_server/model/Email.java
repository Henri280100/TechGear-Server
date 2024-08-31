package com.v01.techgear_server.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "emails")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long emailId;

    @Column(name = "emailAddress", nullable = false)
    private String emailAddress;

    @Column(name = "verificationToken", nullable = false, unique = true)
    private String verificationToken;

    @Column(name = "verified")
    private boolean verified;

    @Column(name = "sentAt")
    private LocalDateTime sentAt;

    @Column(name = "verifiedAt")
    private LocalDateTime verifiedAt;

    @Column(name = "recipient")
    private String recipient;
    
    @Column(name = "msgBody")
    private String msgBody;
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "attachment")
    private String attachment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
