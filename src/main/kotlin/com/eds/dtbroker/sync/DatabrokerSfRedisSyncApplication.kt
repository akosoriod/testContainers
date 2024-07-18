package com.eds.dtbroker.sync

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DatabrokerSfRedisSyncApplication

fun main(args: Array<String>) {
    runApplication<DatabrokerSfRedisSyncApplication>(*args)
}