package com.v01.techgear_server.config;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.product.dto.ProductSearchRequest;
import com.v01.techgear_server.enums.Category;
import com.v01.techgear_server.exception.GenerateHashKeyException;
import com.v01.techgear_server.product.model.Product;

@Component
public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (params.length == 0) {
            return generateDefaultKey(target, method);
        }
        return generateSpecificKey(target, method, params);
    }

    private String generateDefaultKey(Object target, Method method) {
        return target.getClass().getSimpleName() + "_" + method.getName();
    }

    private String generateSpecificKey(Object target, Method method, Object... params) {
        StringBuilder builder = new StringBuilder();
        builder.append(target.getClass().getSimpleName()).append(".").append(method.getName());

        for (Object param : params) {
            builder.append("_").append(generateParamKey(param));
        }

        return builder.toString();
    }

    private String generateParamKey(Object param) {
        if (param == null)
            return "null";

        // Handle specific types with custom key generation
        if (param instanceof ProductSearchRequest productSearchRequest) {
            return generateProductSearchKey(productSearchRequest);
        }

        if (param instanceof Product product) {
            return generateProductKey(product);
        }

        if (param instanceof Long || param instanceof Integer) {
            return param.toString();
        }

        if (param instanceof String) {
            return param.toString();
        }

        return generateHashKey(param);
    }

    private String generateHashKey(Object param) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String serializedObject = mapper.writeValueAsString(param);
            return DigestUtils.md5DigestAsHex(serializedObject.getBytes());
        } catch (Exception e) {
            throw new GenerateHashKeyException("Error generating hash key for object: " + param, e);
        }
    }

    private String generateProductKey(Product param) {
        return "productId" + param.getProductId() + "|name:" +
		        param.getName() + "|category:" + param.getCategory() + "|price:" +
		        param.getPrice();
    }

    private String generateProductSearchKey(ProductSearchRequest param) {
        return "query:" + (param.getQuery() != null ? param.getQuery() : "") +
		        "|page:" + (param.getPage() != null ? param.getPage() : 0) +
		        "|perPage:" + (param.getPerPage() != null ? param.getPerPage() : 10) +
		        "|categories:" +
		        (param.getCategories() != null
				        ? param.getCategories()
				               .stream()
				               .map(Category::toString)
				               .sorted()
				               .collect(Collectors.joining(","))
				        : "") +
		        "|brands:" +
		        (param.getBrands() != null ? param.getBrands()
		                                          .stream()
		                                          .sorted()
		                                          .collect(Collectors.joining(","))
				        : "") +
		        "|minPrice:" + (param.getMinPrice() != null ? param.getMinPrice() : "") +
		        "|maxPrice:" + (param.getMaxPrice() != null ? param.getMaxPrice() : "");
    }
}