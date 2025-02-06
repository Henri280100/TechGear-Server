package com.v01.techgear_server.serviceimpls;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.v01.techgear_server.dto.ProductDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.typesense.api.Client;
import org.typesense.model.FacetCounts;
import org.typesense.model.SearchParameters;
import org.typesense.model.SearchResult;

import com.v01.techgear_server.dto.ProductSearchRequest;
import com.v01.techgear_server.dto.ProductSearchResponse;
import com.v01.techgear_server.enums.Category;
import com.v01.techgear_server.enums.ProductAvailability;
import com.v01.techgear_server.mapping.ProductMapper;
import com.v01.techgear_server.model.FacetCount;
import com.v01.techgear_server.model.SortOption;
import com.v01.techgear_server.service.searching.services.ProductSearchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {
    private final Client typesenseClient;
    private final ProductMapper productMapper;

    @Override
    public ProductSearchResponse buildSearchResponse(SearchResult searchResult) {
        // Convert hits to ProductDTO
        List<ProductDTO> products = convertSearchResults(searchResult);

        // Build response with additional metadata
        return ProductSearchResponse.builder()
                .products(products)
                .totalResult(searchResult.getFound() != null ? searchResult.getFound() : 0)
                .page(searchResult.getPage())
                .perPage(searchResult.getRequestParams().getPerPage())
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
    @Cacheable(value = "productSearchCache", keyGenerator = "customKeyGenerator", condition = "#request.query != null", unless = "#result.totalResults == 0")
    public CompletableFuture<ProductSearchResponse> searchProduct(ProductSearchRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            SearchParameters searchParameters = new SearchParameters()
                    .q(request.getQuery() != null ? request.getQuery() : "*")
                    .queryBy("name,productDescription,brand,slug,price,category")
                    .page(request.getPage() != null ? request.getPage() : 1)
                    .perPage(request.getPerPage() != null ? request.getPerPage() : 10)
                    .facetBy("category,brand,availability")
                    .maxFacetValues(10);

            validateSearchResponse(searchParameters, request);

            try {
                SearchResult searchResult = typesenseClient.collections("products").documents()
                        .search(searchParameters);
                return buildSearchResponse(searchResult);
            } catch (Exception e) {
                log.error("Search failed", e);
                return ProductSearchResponse.builder()
                        .products(Collections.emptyList())
                        .totalResult(0)
                        .build();
            }
        }, getForkJoinPool());
    }

    private Executor getForkJoinPool() {
        return Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    }

    private void validateSearchResponse(SearchParameters searchParameters, ProductSearchRequest request) {
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            searchParameters.filterBy(categoriesFilter(request.getCategories()));
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

    private String categoriesFilter(List<Category> categories) {
        return String.format("(%s)", categories.stream()
                .map(category -> String.format("category:=%s", category))
                .collect(Collectors.joining(" || ")));
    }

    private String brandsFilter(List<String> brands) {
        return String.format("(%s)", brands.stream()
                .map(brand -> String.format("brand:=%s", brand))
                .collect(Collectors.joining(" || ")));
    }

    private String priceRangeFilter(double minPrice, double maxPrice) {
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