package com.eds.dtbroker.sync

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(RedisRepository::class.java)

    @Transactional
    fun addRecordsToCache(records: List<String>, redisKey: String) {
        val batchSize = 1000  // Adjust batch size as needed
        var count = 0

        redisTemplate.executePipelined(RedisCallback { connection ->
            connection.multi()  // Start the transaction

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

                    // Execute in batches
                    if (index > 0 && index % batchSize == 0) {
                        connection.exec()
                        connection.multi()
                    }
                }
                connection.exec()  // Commit the remaining transactions
                logger.info("Successfully added ${records.size} records to Redis.")
            } catch (e: Exception) {
                connection.discard()  // Rollback the transaction
                logger.error("An error occurred while adding records to Redis: ${e.message}", e)
            }
            null
        })
    }
}