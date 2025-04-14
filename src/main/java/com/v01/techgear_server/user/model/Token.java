package com.v01.techgear_server.user.model;

import com.v01.techgear_server.enums.TokenTypes;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "accessToken", "refreshToken" })
})
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tokenId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "accessToken", length = 1000, nullable = false)
    private String accessToken;

    @Column(name = "refreshToken", length = 1000, nullable = false)
    private String refreshToken;

    @Column(name="tokenType")
    @Enumerated(EnumType.STRING)
    private TokenTypes tokenType;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "expiresIn")
    private Long expiresIn;


    @Column(name="createdDate")
    private Instant createdDate;

    @Column(name="expiresDate")
    private Instant expiresDate;

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer()
                                      .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                         .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        Token token = (Token) o;
        return getTokenId() != null && Objects.equals(getTokenId(), token.getTokenId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                                                                       .getPersistentClass()
                                                                       .hashCode() : getClass().hashCode();
    }
}
