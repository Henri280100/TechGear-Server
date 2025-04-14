package com.v01.techgear_server.common.model.search;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortOption {
    private String field;
    private String order; // desc or asc
}
