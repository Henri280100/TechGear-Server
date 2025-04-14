package com.v01.techgear_server.product.dto;

import java.util.List;
import java.util.Map;

import com.v01.techgear_server.common.model.search.FacetCount;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSearchResponse {
    private List<ProductDTO> products;
    private long totalResult;
    private Integer page;
    private Integer perPage;
    private Map<String, List<FacetCount>> facets;
}