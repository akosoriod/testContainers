package com.ciandt.snow_to_redis_sync

import io.lettuce.core.RedisClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy

@Configuration
class RedisConfig(
    @Value("\${redis.uri}") private val redisUri: String
) {

    private val redisClient: RedisClient = RedisClient.create(redisUri)

    @Bean
    fun redisClient(): RedisClient {
        return redisClient
    }

    @PreDestroy
    fun shutdown() {
        redisClient.shutdown()
    }
}