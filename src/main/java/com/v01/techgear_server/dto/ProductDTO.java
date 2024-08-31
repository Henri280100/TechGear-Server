package com.v01.techgear_server.dto;

import java.util.ArrayList;

import com.v01.techgear_server.enums.Category;

import lombok.Data;

@Data
public class ProductDTO {
    private Long product_id;
    private String name;
    private ImageDTO image;
    private double price;
    // private ArrayList<ProductSpecificationDTO> specifications;
    private ProductSpecificationDTO specifications;
    private Category category;
    private ArrayList<ReviewsDTO> reviews;
    private ProductDetailDTO productDetail;
}
