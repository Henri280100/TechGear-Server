package com.v01.techgear_server.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortOption {
    private String field;
    private String order; // desc or asc
}
