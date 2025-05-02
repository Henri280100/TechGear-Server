package com.v01.techgear_server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Configuration
@SuppressWarnings("deprecation")
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig extends CachingConfigurerSupport {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    @Lazy
    RedisTemplate<String, Object> redisTemplate() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(mapper, Object.class);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setDefaultSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }


    @Bean
    @Lazy
    LettucePoolingClientConfiguration lettucePoolConfig() {
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(new GenericObjectPoolConfig<>())
                .build();
    }

    @Bean
    @Lazy
    LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort),
                lettucePoolConfig());
    }

    @Bean
    @Lazy
    CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {

        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(
                Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeValuesWith(
                        SerializationPair.fromSerializer(
                                redisTemplate.getValueSerializer()));

        return RedisCacheManager.builder(redisTemplate.getConnectionFactory())
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(specificCacheConfigurations(redisTemplate))
                .cacheWriter(writer)
                .build();
    }

    private Map<String, RedisCacheConfiguration> specificCacheConfigurations(
            RedisTemplate<String, Object> redisTemplate) {
        return Map.of(

                "productSearchCache", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(30))
                        .disableCachingNullValues()
                        .serializeValuesWith(SerializationPair.fromSerializer(
                                redisTemplate.getValueSerializer())),

                "productCache", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(4))
                        .disableCachingNullValues()
                        .serializeValuesWith(SerializationPair.fromSerializer(
                                redisTemplate.getValueSerializer())),

                "productDetailCache", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(4))
                        .disableCachingNullValues()
                        .serializeValuesWith(SerializationPair.fromSerializer(
                                redisTemplate.getValueSerializer()))

        );


    }
}
