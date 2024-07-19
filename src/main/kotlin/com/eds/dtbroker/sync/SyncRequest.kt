package com.eds.dtbroker.sync

data class SyncRequest(
    val schema: String,
    val snowflakeTable: String,
    val redisKeyPrefix: String
)