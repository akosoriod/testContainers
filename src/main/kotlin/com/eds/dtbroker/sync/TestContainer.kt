package com.eds.dtbroker.sync

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestContainer

fun main(args: Array<String>) {
    runApplication<TestContainer>(*args)
}