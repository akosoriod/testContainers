package com.eds.dtbroker.sync.resource

import com.eds.dtbroker.sync.model.SyncRequest
import com.eds.dtbroker.sync.service.SyncService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class UserVehicleRecordController(private val service: SyncService) {

    @PostMapping("/sync")
    fun syncData(@RequestBody syncRequest: SyncRequest): String {
        val result = service.syncData(syncRequest)
        return result
    }

    // @GetMapping("/info")
    // fun getRedisInfo(): String {
    //     return service.getRedisInfo()
    // }

    // @GetMapping("/flush")
    // fun flushAll(): String {
    //     return service.flushAll()
    // }
}