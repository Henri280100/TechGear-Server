package com.v01.techgear_server.shared.event.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class kafkaTopicConfig {
	
	@Bean
	public NewTopic productEventTopic() {
		return TopicBuilder.name("product.events").partitions(1).replicas(1).build();
	}
}
