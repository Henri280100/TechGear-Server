package com.v01.techgear_server.shared.event.consumer.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.shared.event.dto.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ProductEventConsumer {
	
	@KafkaListener(topics = "product.events", groupId = "${spring.kafka.consumer.group-id}")
	public void consume(String message) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ProductEvent event = objectMapper.readValue(message, ProductEvent.class);
			log.info("Consumed event: {}", event);
			
			switch (event.getEventType()) {
				case IMAGE_UPDATED:
					handleImageUpdate(event);
					break;
				case PRICE_CHANGED:
					handlePriceChange(event);
					break;
				case FINAL_PRICE_CHANGED:
					handleFinalPriceChange(event);
					break;
				// Add other cases
			}
		} catch (JsonProcessingException e) {
			log.error("Failed to parse Kafka event", e);
		}
	}
	
	private void handleFinalPriceChange(ProductEvent event) {
		BigDecimal discountedPrice = (BigDecimal) event.getPayload().get("finalPrice");
		Long productId = event.getProductId();
		log.info("Updating final price for product ID: {} with new discount price: {}", productId, discountedPrice);
	}
	
	private void handlePriceChange(ProductEvent event) {
		BigDecimal newPrice = (BigDecimal) event.getPayload().get("finalPrice");
		Long productId = event.getProductId();
		log.info("Updating price for product ID: {} with new price: {}", productId, newPrice);
	}
	
	private void handleImageUpdate(ProductEvent event) {
		String newImage = (String) event.getPayload().get("imageUrl");
		Long productId = event.getProductId();
		log.info("Updating image for product ID: {} with new image URL: {}", productId, newImage);
	}
}
