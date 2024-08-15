package com.eds.dtbroker.sync

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.containers.GenericContainer
import org.springframework.http.MediaType

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ExtendWith(SpringExtension::class)
class ProductControllerIntegrationTest {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @Container
    private val redisContainer = GenericContainer<Nothing>("redis:latest").apply {
        withExposedPorts(6223   )
    }


    @BeforeEach
    fun setUp() {
        // Limpiar datos existentes
    //    productRepository.deleteAll()
    }

    @Test
    fun `should return all products`() {
        mockMvc.get("/products")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
    }

    @Test
    fun `should create a product`() {
        val productJson = """{"id": "1", "name": "Product A", "price": 10.0}"""

        mockMvc.post("/products") {
            contentType = MediaType.APPLICATION_JSON
            content = productJson
        }.andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Product A") }
            jsonPath("$.price") { value(10.0) }
        }
    }
}