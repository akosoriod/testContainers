package com.eds.dtbroker.sync.repository

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(RedisRepository::class.java)

    fun addRecordsToCache(records: List<String>, redisKey: String) {
        val batchSize = 10000  // Adjust batch size as needed

        redisTemplate.executePipelined(RedisCallback { connection ->
            try {
                records.forEachIndexed { index, record ->
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

                    // Optionally, execute in batches
                    if (index > 0 && index % batchSize == 0) {
                        // Batch processing logic (not needed here as executePipelined handles batching internally)
                    }
                }
                logger.info("Successfully added ${records.size} records to Redis.")
            } catch (e: Exception) {
                logger.error("An error occurred while adding records to Redis: ${e.message}", e)
            }
            null
        })
    }
}
