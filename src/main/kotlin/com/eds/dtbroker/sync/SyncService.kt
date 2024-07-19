package com.eds.dtbroker.sync

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SyncService(
    val snowflake: SnowflakeRepository,
    val redis: RedisRepository,
) {
    private val logger = LoggerFactory.getLogger(SyncService::class.java)

    fun syncData() {
        try {
            val records = snowflake.getAllRecords("schema", "table")
            logger.info("Loaded ${records.size} records from Snowflake")
            
            // redis.addRecordsToCache(records)
        } catch (e: Exception) {
            logger.error("An error occurred: ${e.message}", e)
        }
    }

    fun getRedisInfo(): String {
        logger.info("Fetching Redis info")
        // TODO add redis info implementation
        return ""
    }

    fun flushAll(): String {
        logger.info("Flushing all data from Redis")
        // TODO add flush implementation
        return ""
    }
}