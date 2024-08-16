package com.eds.dtbroker.sync

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.containers.GenericContainer
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.wait.strategy.Wait
import kotlin.time.Duration

@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    companion object {
        @Container
        val redisContainer = GenericContainer<Nothing>("redis:latest").apply {
            withExposedPorts(6000)
            withCommand("redis-server --port 6000")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6000) } // Map to the exposed port
        }
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

        // Optionally verify the product was saved
        val savedProduct = productRepository.findById("1")
        assert(savedProduct.isPresent)
        assert(savedProduct.get().name == "Product A")
        assert(savedProduct.get().price == 10.0)
    }
}