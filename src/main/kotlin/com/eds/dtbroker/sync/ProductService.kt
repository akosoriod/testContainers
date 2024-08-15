package com.eds.dtbroker.sync

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(private val productRepository: ProductRepository) {

    fun findAllProducts(): List<Product> {
        return productRepository.findAll().toList() // Convertimos Iterable a List
    }

    fun saveProduct(product: Product): Product {
        return productRepository.save(product)
    }
}