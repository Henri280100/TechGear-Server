package com.v01.techgear_server.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "mediaFilename")
    private String mediaFilename;
    
    @Column(name = "mediaContentType")
    private String mediaContentType;
    
    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;
}
