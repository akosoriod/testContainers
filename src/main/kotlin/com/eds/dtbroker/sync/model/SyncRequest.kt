package com.eds.dtbroker.sync.model

data class SyncRequest(
    val schema: String,
    val snowflakeTable: String,
    val redisKey: String
)