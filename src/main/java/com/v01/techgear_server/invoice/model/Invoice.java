package com.v01.techgear_server.invoice.model;

import com.v01.techgear_server.enums.InvoiceStatus;
import com.v01.techgear_server.order.model.Order;
import com.v01.techgear_server.user.model.AccountDetails;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "invoice")
public class Invoice implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "issue_date")
    private String issueDate;

    /**
     * Represents the date of the invoice.
     */
    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    /**
     * Represents the total amount of the invoice.
     */
    @Column(name = "total_amount")
    private Integer totalAmount;

    @ManyToOne
    @JoinColumn(name = "account_details_id", nullable = false)
    private AccountDetails accountDetails;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<InvoiceDetails> invoiceDetails = new ArrayList<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Invoice invoice = (Invoice) o;
        return getInvoiceId() != null && Objects.equals(getInvoiceId(), invoice.getInvoiceId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }
}
