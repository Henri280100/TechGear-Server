package com.v01.techgear_server.shared.event.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ProductEventType {
	IMAGE_UPDATED("IMAGE_UPDATED"),
	PRODUCT_CREATED("PRODUCT_CREATED"),
	PRODUCT_UPDATED("PRODUCT_UPDATED"),
	PRODUCT_DELETED("PRODUCT_DELETED"),
	FINAL_PRICE_CHANGED("FINAL_PRICE_CHANGED"),
	PRICE_CHANGED("PRICE_CHANGED");
	
	private final String eventType;
	
	ProductEventType(String eventType) {
		this.eventType = eventType;
	}
	
	@JsonCreator
	public static ProductEventType fromValue(String value) {
		for (ProductEventType eventType : ProductEventType.values()) {
			if (eventType.eventType.equalsIgnoreCase(value)) {
				return eventType;
			}
		}
		throw new IllegalArgumentException("Unknown ProductEventType: " + value);
	}
	}
