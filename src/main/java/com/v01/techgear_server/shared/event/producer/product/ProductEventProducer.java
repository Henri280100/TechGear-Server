package com.v01.techgear_server.shared.event.producer.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.v01.techgear_server.shared.event.dto.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	
	public void publishEvent(String topic, String eventType, Object event) {
		try {
			String eventJson = objectMapper.writeValueAsString(event);
			kafkaTemplate.send(topic, eventType, eventJson);
			log.info("Published event to topic {}: {}", topic, eventJson);
		} catch (Exception e) {
			log.error("Failed to publish event to topic {}: {}", topic, e.getMessage(), e);
		}
	}
	
	public void publishProductEvent(ProductEvent event) {
		try {
			String message = objectMapper.writeValueAsString(event);
			kafkaTemplate.send("product.events", event.getProductId().toString(), message);
			log.info("Published product event: {}", event);
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize product event", e);
		}
	}
}
