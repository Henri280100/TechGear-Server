package com.v01.techgear_server.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacetCount {
    private String value;
    private int count;
}