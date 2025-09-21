package com.redis.cache_demo.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redis.cache_demo.dto.ProductRecord;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, false); // Important for records

        Jackson2JsonRedisSerializer<ProductRecord> serializer = new Jackson2JsonRedisSerializer<>(objectMapper(), ProductRecord.class);

        RedisCacheConfiguration redisConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(2))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                );

        return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(redisConfig).build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure ObjectMapper
        Jackson2ObjectMapperBuilder mapper1 = Jackson2ObjectMapperBuilder.json();
        mapper1.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper1.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper1.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        mapper1.featuresToDisable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES);
        mapper1.modules(new JavaTimeModule());

        ObjectMapper mapper = new ObjectMapper();
        // Register necessary modules
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, false);
        mapper.configure(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES, false);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        // Use customized serializer
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

//    @Bean
//    public ObjectMapper objectMapper11() {
//        return (new Jackson2ObjectMapperBuilder())
//                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
//                        MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES,
//                        MapperFeature.DEFAULT_VIEW_INCLUSION,
//                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//                .modules(new Module[]{new Jdk8Module(), new JavaTimeModule(), new ParameterNamesModule()})
//                .build();
//    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
                .addModule(new JavaTimeModule())
                .findAndAddModules()
                .build();
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        var jacksonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper());
        var valueSerializer = RedisSerializationContext.SerializationPair.fromSerializer(jacksonSerializer);

        return (builder) -> builder
                .withCacheConfiguration("PRODUCT_CACHE",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(valueSerializer)
                                .entryTtl(Duration.ofMinutes(2)));
    }

}
