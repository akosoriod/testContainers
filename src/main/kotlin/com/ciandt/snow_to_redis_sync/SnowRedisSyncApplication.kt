package com.ciandt.snow_to_redis_sync

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SnowRedisSyncApplication

fun main(args: Array<String>) {
    runApplication<SnowRedisSyncApplication>(*args)
}