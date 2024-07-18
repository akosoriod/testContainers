package com.eds.dtbroker.databroker_sf_redis_sync

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DatabrokerSfRedisSyncApplication

fun main(args: Array<String>) {
    runApplication<DatabrokerSfRedisSyncApplication>(*args)
}