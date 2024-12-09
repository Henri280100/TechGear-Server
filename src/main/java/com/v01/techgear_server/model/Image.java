package com.v01.techgear_server.model;


import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.v01.techgear_server.enums.ImageTypes;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
public class Image implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="image_url")
    private String imageUrl;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "content_type")
    @Pattern(regexp = "^image/(jpeg|png|gif|webp|bmp)$", 
             message = "Invalid image content type")
    private String contentType;

    @Column(name = "data", columnDefinition="BYTEA")
    private byte[] data; // Store image data as byte array

    // Metadata fields
    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    // Auditing fields
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name="last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    // Enum for image type
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type")
    private ImageTypes imageTypes;

    @OneToOne(mappedBy="image", cascade = CascadeType.ALL)
    private AccountDetails accountDetails;

    @OneToOne(mappedBy="image", cascade = CascadeType.ALL)
    private Product product;

    @OneToOne(mappedBy="image", cascade = CascadeType.ALL)
    private ProductRating productRating;

    @OneToOne(mappedBy="image", cascade = CascadeType.ALL)
    private ProductSpecification productSpecification;

    @OneToOne(mappedBy="image", cascade = CascadeType.ALL)
    private Wishlist wishlist;

    @OneToOne(mappedBy="image", cascade = CascadeType.ALL)
    private WishlistItems wishlistItems;
}

