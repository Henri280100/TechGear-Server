package com.v01.techgear_server.product.search.impl;

import com.v01.techgear_server.common.model.search.FacetCount;
import com.v01.techgear_server.common.model.search.SortOption;
import com.v01.techgear_server.enums.ProductAvailability;
import com.v01.techgear_server.exception.ProductSearchException;
import com.v01.techgear_server.product.dto.ProductCategoryDTO;
import com.v01.techgear_server.product.dto.ProductDTO;
import com.v01.techgear_server.product.dto.ProductSearchRequest;
import com.v01.techgear_server.product.dto.ProductSearchResponse;
import com.v01.techgear_server.product.mapping.ProductMapper;
import com.v01.techgear_server.product.search.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.typesense.api.Client;
import org.typesense.model.FacetCounts;
import org.typesense.model.SearchParameters;
import org.typesense.model.SearchResult;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {
    private static final Executor SEARCH_EXECUTOR = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    private final Client typesenseClient;
    private final ProductMapper productMapper;

    @Override
    public ProductSearchResponse buildSearchResponse(SearchResult searchResult) {
        // Convert hits to ProductDTO
        List<ProductDTO> products = convertSearchResults(searchResult);

        // Build response with additional metadata
        return ProductSearchResponse.builder()
                .product(products)
                .totalResult(searchResult.getFound() != null ? searchResult.getFound() : 0)
                .page(searchResult.getPage() != null ? searchResult.getPage() : 1)
                .perPage(searchResult.getRequestParams() != null && searchResult.getRequestParams().getPerPage() != null ? searchResult.getRequestParams().getPerPage() : 10)
                .facets(extractFacets(searchResult))
                .build();
    }

    private Map<String, List<FacetCount>> extractFacets(SearchResult searchResult) {
        Map<String, List<FacetCount>> facets = new HashMap<>();

        List<FacetCounts> facetCounts = searchResult.getFacetCounts();
        if (facetCounts != null) {
            for (FacetCounts facetCount : facetCounts) {
                List<FacetCount> counts = facetCount.getCounts().stream()
                        .map(count -> new FacetCount(
                                count.getValue(),
                                count.getCount()))
                        .toList();

                facets.put(facetCount.getFieldName(), counts);
            }
        }

        return facets;
    }

    private List<ProductDTO> convertSearchResults(SearchResult searchResult) {
        return searchResult.getHits() == null ? Collections.emptyList()
                : searchResult.getHits().stream()
                .map(hit -> productMapper.toSearchDTO(hit.getDocument()))
                .filter(Objects::nonNull)
                .toList();
    }


    @Override
    @Cacheable(value = "productSearchCache", keyGenerator = "customKeyGenerator", condition = "#request.query != null", unless = "#result.totalResult == 0")
    public CompletableFuture<ProductSearchResponse> searchProduct(ProductSearchRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            SearchParameters searchParameters = new SearchParameters()
                    .q(Optional.ofNullable(request.getQuery()).filter(q -> !q.trim().isEmpty()).orElse("*"))
                    .queryBy("name,productDescription,brand,category")
                    .page(Optional.ofNullable(request.getPage()).filter(p -> p > 0).orElse(1))
                    .perPage(Optional.ofNullable(request.getPerPage()).filter(pp -> pp > 0 && pp <= 100).orElse(8))
                    .facetBy("category,brand,availability,finalPrice")
                    .maxFacetValues(10)
                    .includeFields("productId,name,productDescription,price,category,brand,availability,features,image") // Updated to match schema
                    .highlightFields("name,productDescription") // Removed price from highlighting
                    .numTypos("2")
                    .exhaustiveSearch(false);

            log.info("Search parameters: query={}, page={}, perPage={}", searchParameters.getQ(), searchParameters.getPage(), searchParameters.getPerPage());
            validateSearchResponse(searchParameters, request);

            try {
                SearchResult searchResult = typesenseClient.collections("product")
                        .documents()
                        .search(searchParameters);
                return buildSearchResponse(searchResult);
            } catch (Exception e) {
                log.error("Search failed for query: {}", request.getQuery(), e);
                throw new ProductSearchException("Search failed", e);
            }
        }, SEARCH_EXECUTOR);
    }

    private void validateSearchResponse(SearchParameters searchParameters, ProductSearchRequest request) {
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            searchParameters.filterBy(categoriesFilter(request.getCategories().stream().toList()));
        }

        if (request.getBrands() != null && !request.getBrands().isEmpty()) {
            searchParameters.filterBy(brandsFilter(request.getBrands()));
        }

        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            searchParameters.filterBy(priceRangeFilter(request.getMinPrice(), request.getMaxPrice()));
        }

        if (request.getAvailability() != null) {
            searchParameters.filterBy(availabilityFilter(request.getAvailability()));
        }

        if (request.getMinStockLevel() != null) {
            searchParameters.filterBy(minStockLevelFilter(request.getMinStockLevel()));
        }

        if (request.getSortOption() != null && !request.getSortOption().isEmpty()) {
            searchParameters.sortBy(sortBy(request.getSortOption()));
        }
    }

    private String categoriesFilter(List<ProductCategoryDTO> categories) {
        return String.format("(%s)", categories.stream()
                .map(category -> String.format("category:=%s", category.getProductCategoryName()))
                .collect(Collectors.joining(" || ")));
    }

    private String brandsFilter(List<String> brands) {
        return String.format("(%s)", brands.stream()
                .map(brand -> String.format("brand:=%s", brand))
                .collect(Collectors.joining(" || ")));
    }

    private String priceRangeFilter(double minPrice, double maxPrice) {
        // Ensure valid range and format as float-compatible strings
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }
        return String.format("price:>=%f && price:<=%f", minPrice, maxPrice);
    }

    private String availabilityFilter(ProductAvailability availability) {
        return String.format("availability:=%s", availability.name());
    }

    private String minStockLevelFilter(int minStockLevel) {
        return String.format("stockLevel:>=%d", minStockLevel);
    }

    private String sortBy(List<SortOption> sortOptions) {
        return String.join(",", sortOptions.stream()
                .map(sort -> String.format("%s:%s", sort.getField(), sort.getOrder()))
                .toList());
    }
}

