package com.eds.dtbroker.sync.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    @Value("\${redis.batch-size}") private val batchSize: Int
) {
    private val logger = LoggerFactory.getLogger(RedisRepository::class.java)

    fun addRecordsToCache(records: List<String>, redisKey: String) {
        records.chunked(batchSize).forEach { batch ->
            redisTemplate.executePipelined(RedisCallback { connection ->
                try {
                    batch.forEach { record ->
                        val jsonNode = objectMapper.readTree(record)
                        val keyNode = jsonNode.get(redisKey)

                        if (keyNode != null) {
                            val key = keyNode.asText()
                            val serializedKey = redisTemplate.stringSerializer.serialize(key) ?: ByteArray(0)
                            val serializedValue = redisTemplate.stringSerializer.serialize(record) ?: ByteArray(0)
                            connection.set(serializedKey, serializedValue)
                        } else {
                            logger.warn("Key '$redisKey' not found in record: $record")
                        }
                    }
                    logger.info("Successfully added ${batch.size} records to Redis.")
                } catch (e: Exception) {
                    logger.error("An error occurred while adding records to Redis: ${e.message}", e)
                }
                null
            })
        }
    }
}