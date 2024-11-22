package com.v01.techgear_server.model;

import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_logs")
public class PaymentLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @Column(name = "payment_log_message")
    private String paymentLogMessage;

    @Column(name = "logs_timestamp")
    private LocalDateTime logsTimestamp;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id")
    private Payment payment;
}
