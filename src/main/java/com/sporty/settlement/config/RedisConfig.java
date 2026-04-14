package com.sporty.settlement.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder;

import java.io.IOException;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.port:6370}")
    private int redisPort;

    private RedisServer redisServer;
    private final JsonMapper objectMapper;

    public RedisConfig(JsonMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void startEmbeddedRedis() throws IOException {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopEmbeddedRedis() throws IOException {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", redisPort));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        final var redisObjectMapper = getObjectMapper();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJacksonJsonRedisSerializer(redisObjectMapper));
        template.setHashValueSerializer(new GenericJacksonJsonRedisSerializer(redisObjectMapper));
        return template;
    }

    private JsonMapper getObjectMapper() {
        final var validator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubTypeIsArray()
                .allowIfSubType("com.")
                .allowIfSubType("java.")
                .allowIfSubType("org.")
                .build();

        final var typeResolver =
                new RecordAwareTypeResolverBuilder(validator, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        return objectMapper
                .rebuild()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setDefaultTyping(typeResolver)
                .build();
    }

    static class RecordAwareTypeResolverBuilder extends DefaultTypeResolverBuilder {

        RecordAwareTypeResolverBuilder(
                final PolymorphicTypeValidator validator,
                final DefaultTyping defaultTyping,
                final JsonTypeInfo.As includeAs) {
            super(validator, defaultTyping, includeAs);
        }

        @Override
        public boolean useForType(final JavaType t) {
            if (t.getRawClass().isRecord()) {
                return true;
            }
            return super.useForType(t);
        }
    }
}
