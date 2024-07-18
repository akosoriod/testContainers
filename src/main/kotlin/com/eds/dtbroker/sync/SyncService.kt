package com.eds.dtbroker.sync

import io.lettuce.core.RedisClient
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.snowflake.client.jdbc.SnowflakeDriver
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

@Service
class SyncService(
    val snowflake: SnowflakeRepository,
    val redis: RedisRepository,
) {
    private val logger = LoggerFactory.getLogger(SyncService::class.java)

    fun syncData() {
        try {
            val records = snowflake.getAllRecords("schema", "table")
            redis.addRecordsToCache(records)
        }
        catch (e: Exception) {
            logger.error("An error occurred: ${e.message}", e)
        }

        fun getRedisInfo(): String {
            logger.info("Fetching Redis info")
           //TODO add redis info implementation
           return ""
        }
    
        fun flushAll(): String {
            logger.info("Flushing all data from Redis")
            // TODO add flush implementation
            return ""
        }

    }
}