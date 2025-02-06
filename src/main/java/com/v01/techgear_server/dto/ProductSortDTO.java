package com.v01.techgear_server.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSortDTO {
    private String field;
    private String direction;
    
}