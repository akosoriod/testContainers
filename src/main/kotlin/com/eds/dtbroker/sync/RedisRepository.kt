package com.eds.dtbroker.sync

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class RedisRepository(
    val redisTemplate: StringRedisTemplate,
){

    // Adds a list of JSON records to REDIS 
    fun addRecordsToCache(records : List<String>){
        //TODO implement redis operations logic here
        redisTemplate.opsForValue()
    }
}