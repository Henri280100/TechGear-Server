package com.v01.techgear_server.controller.Product;

import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.config.GraphQLConfig;
import com.v01.techgear_server.dto.ProductDTO;
import com.v01.techgear_server.service.ProductService;

import graphql.ExecutionResult;
import graphql.GraphQLError;
import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/api/v01/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    private GraphQLConfig graphQLConfig;

    private static Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService, GraphQLConfig graphQLConfig) {
        this.productService = productService;
        this.graphQLConfig = graphQLConfig;
    }

    /**
     * Create a new product.
     *
     * @param productJson JSON string representing the product to be created.
     * @param image       The image file to be uploaded and associated with the
     *                    product.
     * @return A ResponseEntity containing the created ProductDTO, or a 400 Bad
     *         Request
     *         status if an error occurs.
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @PostMapping("/new")
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart("image") MultipartFile image) throws JsonMappingException, JsonProcessingException {
        if (image == null || image.isEmpty()) {
            LOGGER.error("Image file is required");
            return ResponseEntity.badRequest().body(null); // or return an appropriate error message
        }

        // Parse ProductDTO from JSON
        ProductDTO productDTO = new ObjectMapper().readValue(productJson, ProductDTO.class);

        // Create product
        ProductDTO createdProduct = productService.createProduct(productDTO, image);

        LOGGER.debug("Create Product with id: {}", createdProduct.getProduct_id());
        return ResponseEntity.ok(createdProduct);

    }

    /**
     * Deletes a product with the given ID.
     *
     * @param id the ID of the product to delete
     * @return a ResponseEntity indicating success or failure
     * 
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        LOGGER.info("Attempting to delete product with id: {}", id);
        boolean exists = productService.deleteProduct(id);

        if (!exists) {
            LOGGER.warn("Attempted to delete non-existing product with id: {}", id);
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }

        productService.deleteProduct(id);

        LOGGER.info("Successfully deleted product with id: {}", id);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.NO_CONTENT);

    }

    // Should consider about the update all the product method
    // If update all the data in the product,
    // maybe it's not a good idea to rewrite all the data
    @SuppressWarnings("unchecked")
    @PostMapping(value = "/update")
    @MutationMapping(name = "updateProduct")
    public String updateProduct(@RequestBody String queryVal) throws IOException, java.io.IOException {
        LOGGER.info("Received query: " + queryVal); // Log received query

        ExecutionResult executionResult = graphQLConfig.graphQL().execute(queryVal);
        Map<String, Object> respMap = (Map<String, Object>) executionResult.getData();

        LOGGER.info("Execution result: " + respMap); // Log execution result

        List<GraphQLError> errors = executionResult.getErrors();
        if (!errors.isEmpty()) {
            return "{\"error\": \"" + errors.get(0).getMessage() + "\"}";
        }

        String retVal;
        try {
            retVal = new ObjectMapper().writeValueAsString(respMap);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to process query result", e);
            throw new BadRequestException("Failed to process query result");
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
    @PostMapping(value = "/update/product-name")
    @MutationMapping(name = "updateProductName")
    public String updateProductName(@RequestBody String queryVal) throws IOException, java.io.IOException {
        LOGGER.info("Received query: " + queryVal); // Log received query

        ExecutionResult executionResult = graphQLConfig.graphQL().execute(queryVal);
        Map<String, Object> respMap = (Map<String, Object>) executionResult.getData();

        LOGGER.info("Execution result: " + respMap); // Log execution result

        List<GraphQLError> errors = executionResult.getErrors();
        if (!errors.isEmpty()) {
            return "{\"error\": \"" + errors.get(0).getMessage() + "\"}";
        }

        String retVal;
        try {
            retVal = new ObjectMapper().writeValueAsString(respMap);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to process query result", e);
            throw new BadRequestException("Failed to process query result");
        }
        return retVal;
    }

    

    @SuppressWarnings("unchecked")
    @PostMapping(value = "/price")
    @MutationMapping(name = "updateProductPrice")
    public String updateProductPrice(@RequestBody String queryVal) throws IOException, java.io.IOException {
        ExecutionResult executionResult = graphQLConfig.graphQL().execute(queryVal);
        Map<String, Object> respMap = (Map<String, Object>) executionResult.getData();

        List<GraphQLError> errors = executionResult.getErrors();
        if (!errors.isEmpty()) {
            throw new BadRequestException("GraphQL Error: " + errors.get(0).getMessage());
        }

        try {
            return new ObjectMapper().writeValueAsString(respMap);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to process GraphQL response", e);
        }
    }

    /**
     * Fetches all products.
     *
     * @return a ResponseEntity containing a list of ProductDTOs, or a 400 Bad
     *         Request status if an error occurs.
     * @throws java.io.IOException 
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    @PostMapping(value = "/allProducts")
    @QueryMapping(name = "getAllProducts")
    public String getAllProducts(@RequestBody String queryVal) throws IOException, java.io.IOException {
        LOGGER.info("Received query: " + queryVal); // Log received query

        ExecutionResult executionResult = graphQLConfig.graphQL().execute(queryVal);
        Map<String, Object> respMap = (Map<String, Object>) executionResult.getData();

        LOGGER.info("Execution result: " + respMap); // Log execution result

        List<GraphQLError> errors = executionResult.getErrors();
        if (!errors.isEmpty()) {
            return "{\"error\": \"" + errors.get(0).getMessage() + "\"}";
        }

        String retVal;
        try {
            retVal = new ObjectMapper().writeValueAsString(respMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new BadRequestException("Failed to fetch products");
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
    @PostMapping(value = "/product/name")
    @QueryMapping(name = "getProductByName")
    public String getProductByName(@RequestBody String queryVal) throws IOException, java.io.IOException {
        LOGGER.info("Received query: " + queryVal); // Log received query

        ExecutionResult executionResult = graphQLConfig.graphQL().execute(queryVal);
        Map<String, Object> respMap = (Map<String, Object>) executionResult.getData();

        LOGGER.info("Execution result: " + respMap); // Log execution result

        List<GraphQLError> errors = executionResult.getErrors();
        if (!errors.isEmpty()) {
            return "{\"error\": \"" + errors.get(0).getMessage() + "\"}";
        }

        String retVal;
        try {
            retVal = new ObjectMapper().writeValueAsString(respMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new BadRequestException("Failed to fetch products");
        }
        return retVal;
    }

    /**
     * Fetches a product with the given ID.
     *
     * @param id the ID of the product to fetch
     * @return a ResponseEntity containing the ProductDTO, or a 400 Bad Request
     *         status if an error occurs.
     * @throws java.io.IOException 
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    @PostMapping(value = "/product/id:{product_id}")
    @QueryMapping(name = "getProductById")
    public String getProductByID(@RequestBody String queryVal) throws IOException, java.io.IOException {
        LOGGER.info("Received query: " + queryVal); // Log received query

        ExecutionResult executionResult = graphQLConfig.graphQL().execute(queryVal);
        Map<String, Object> respMap = (Map<String, Object>) executionResult.getData();

        LOGGER.info("Execution result: " + respMap); // Log execution result

        List<GraphQLError> errors = executionResult.getErrors();
        if (!errors.isEmpty()) {
            return "{\"error\": \"" + errors.get(0).getMessage() + "\"}";
        }

        String retVal;
        try {
            retVal = new ObjectMapper().writeValueAsString(respMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new BadRequestException("Failed to fetch product id");
        }
        return retVal;
    }

}
