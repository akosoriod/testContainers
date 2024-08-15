package com.eds.dtbroker.sync

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@RedisHash("products")
data class Product(
    @Id val id: String? = null,
    val name: String,
    val price: Double
)



@Repository
interface ProductRepository : CrudRepository<Product, String>