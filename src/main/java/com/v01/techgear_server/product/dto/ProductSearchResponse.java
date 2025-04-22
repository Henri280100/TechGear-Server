package com.v01.techgear_server.product.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.v01.techgear_server.common.model.search.FacetCount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonDeserialize(builder = ProductSearchResponse.ProductSearchResponseBuilder.class)
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchResponse {
    private List<ProductDTO> product;
    private long totalResult;
    private Integer page;
    private Integer perPage;
    private Map<String, List<FacetCount>> facets;
    @JsonPOJOBuilder(withPrefix = "")
    public static class ProductSearchResponseBuilder {}
}