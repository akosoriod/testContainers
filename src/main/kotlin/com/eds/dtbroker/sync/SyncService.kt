package com.eds.dtbroker.sync

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SyncService(
    private val snowflake: SnowflakeRepository,
    private val redis: RedisRepository,
) {
    private val logger = LoggerFactory.getLogger(SyncService::class.java)

    fun syncData(syncRequest: SyncRequest): String {
        return try {
            // Fetch records from Snowflake
            val records = snowflake.getAllRecords(syncRequest.schema, syncRequest.snowflakeTable)
            val message = "Loaded ${records.size} records from Snowflake"
            logger.info(message)

            // Generate Redis keys and prepare records for caching
            val recordsWithKeys = records.map { record ->
                val jsonNode = ObjectMapper().readTree(record)
                val userId = jsonNode.get("USERID")?.asText() ?: "0"
                val key = "${syncRequest.redisKey}_${userId}"
                Pair(key, record)
            }

            val recordsToCache = recordsWithKeys.map { it.second }

            // Add records to Redis with the generated keys
            redis.addRecordsToCache(recordsToCache, syncRequest.redisKey)

            message
        } catch (e: Exception) {
            logger.error("An error occurred: ${e.message}", e)
            ""
        }
    }
}