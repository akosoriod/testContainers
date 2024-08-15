package com.eds.dtbroker.sync

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    @GetMapping
    fun getAllProducts(): List<Product> {
        return productService.findAllProducts()
    }

    @PostMapping
    fun createProduct(@RequestBody product: Product): Product {
        return productService.saveProduct(product)
    }
}