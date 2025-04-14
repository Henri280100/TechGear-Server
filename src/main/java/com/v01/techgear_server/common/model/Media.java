package com.v01.techgear_server.common.model;

import com.v01.techgear_server.product.model.ProductDetail;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "mediaFilename")
    private String mediaFilename;

    @Column(name="media_url")
    private String mediaUrl;
    
    @Column(name = "mediaContentType")
    private String mediaContentType;
    
    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

    @Column(name="file_size")
    private Long fileSize;

    @Column(name="width")
    private Integer width;

    @Column(name="height")
    private Integer height;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    @ManyToOne
    @JoinColumn(name = "media_id")
    private ProductDetail productDetail;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer()
                .getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Media media = (Media) o;
        return getId() != null && Objects.equals(getId(), media.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();
    }
}
