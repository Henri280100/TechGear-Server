package com.v01.techgear_server.config.Kafka;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new KafkaAdmin(config);
    }

    @Bean
    public AdminClient adminClient() {
        return AdminClient.create(kafkaAdmin().getConfigurationProperties());
    }

    @Bean
    public boolean manageTopics() {
        try {
            AdminClient adminClient = adminClient();

            // Define the topic
            NewTopic emailTopic = new NewTopic("email-topic", 3, (short) 1)
                    .configs(Map.of(
                            "retention.ms", "86400000",
                            "cleanup.policy", "compact"));

            // List existing topics
            ListTopicsResult topics = adminClient.listTopics();
            KafkaFuture<Set<String>> future = topics.names();
            Set<String> existingTopics = future.get();

            // Check if the topic already exists
            if (!existingTopics.contains(emailTopic.name())) {
                CreateTopicsResult createResult = adminClient.createTopics(Collections.singleton(emailTopic));
                createResult.all().get(); // Wait for completion
                return true; // Topic was created
            } else {
                return false; // Topic already exists
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false; // Failure in topic management
        }
    }
}
