package com.ciandt.snow_to_redis_sync

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class UserVehicleRecordController(private val service: UserVehicleRecordService) {

    @GetMapping("/sync")
    fun syncData(): String {
        service.syncData()
        return "Data synchronized successfully"
    }

    @GetMapping("/info")
    fun getRedisInfo(): String {
        return service.getRedisInfo()
    }

    @GetMapping("/flush")
    fun flushAll(): String {
        return service.flushAll()
    }
}