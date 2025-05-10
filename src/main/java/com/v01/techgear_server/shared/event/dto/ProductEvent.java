package com.v01.techgear_server.shared.event.dto;

import com.v01.techgear_server.shared.event.enums.ProductEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEvent {
	/**
	 * Represents the type of event associated with a product.
	 * This field is used to specify the nature of the event, such as creation, update, or deletion.
	 */
	private ProductEventType eventType;
	
	/**
	 * The unique identifier of the product associated with the event.
	 * This field is used to link the event to a specific product.
	 */
	private Long productId;
	
	/**
	 * A map containing additional data related to the event.
	 * The keys represent the names of the data fields, and the values represent their corresponding values.
	 */
	private Map<String, Object> payload;
}
