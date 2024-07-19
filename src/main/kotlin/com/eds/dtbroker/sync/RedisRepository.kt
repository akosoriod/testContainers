package com.eds.dtbroker.sync

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
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
        redisTemplate.execute { connection ->
            connection.multi()  // Start the transaction

            try {
                records.forEach { record ->
                    val jsonNode = objectMapper.readTree(record)
                    val keyNode = jsonNode.get(redisKey)
                    
                    if (keyNode != null) {
                        val key = keyNode.asText()
                        redisTemplate.opsForValue().set(key, record)
                    }
                }
                connection.exec()  // Commit the transaction
                logger.info("Successfully added ${records.size} records to Redis.")
            } catch (e: Exception) {
                connection.discard()  // Rollback the transaction
                logger.error("An error occurred while adding records to Redis: ${e.message}", e)
            }
        }
    }
}