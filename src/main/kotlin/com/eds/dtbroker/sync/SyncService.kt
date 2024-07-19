package com.eds.dtbroker.sync

import com.fasterxml.jackson.databind.node.ObjectNode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SyncService(
    val snowflake: SnowflakeRepository,
    val redis: RedisRepository,
) {
    private val logger = LoggerFactory.getLogger(SyncService::class.java)

    fun syncData(syncRequest: SyncRequest): String {
        return try {
            val records = snowflake.getAllRecords(syncRequest.schema, syncRequest.snowflakeTable)
            val message = "Loaded ${records.size} records from Snowflake"
            logger.info(message)
            return message
        } catch (e: Exception) {
            logger.error("An error occurred: ${e.message}", e)
            ""
        }
    }

    // fun getRedisInfo(infoRequest: InfoRequest): String {
    //     logger.info("Fetching Redis info")
    //     // TODO add redis info implementation
    //     return ""
    // }

    // fun flushAll(flushRequest: FlushRequest): String {
    //     if (!flushRequest.confirm) {
    //         return "Flush not confirmed"
    //     }
    //     logger.info("Flushing all data from Redis")
    //     // TODO add flush implementation
    //     return "All data flushed from Redis successfully"
    // }
}