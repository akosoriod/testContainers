package com.eds.dtbroker.sync

import jakarta.transaction.Transactional
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
import org.testcontainers.containers.PostgreSQLContainer
import org.springframework.http.MediaType


@SpringBootTest(classes = [YourMainApplicationClass::class])
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ExtendWith(SpringExtension::class)
class ClientControllerIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @Container
    private val postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:latest").apply {
        withDatabaseName("integrationTest")
        withUsername("user")
        withPassword("password")
        start()
    }

    @DynamicPropertySource
    fun configureProperties(registry: DynamicPropertyRegistry) {
        registry.add("spring.datasource.url") { postgreSQLContainer.jdbcUrl }
        registry.add("spring.datasource.username") { postgreSQLContainer.username }
        registry.add("spring.datasource.password") { postgreSQLContainer.password }
    }

    @Test
    fun `should return all users`() {
        mockMvc.get("/users")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
            }
    }

    @Test
    fun `should create a user`() {
        val userJson = """{"name": "Joao Felix", "email": "joaoFelix@example.com"}"""

        mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = userJson
        }.andExpect {
            status { isOk() }
            jsonPath("$.name") { value("Joao Felix") }
            jsonPath("$.email") { value("joaoFelix@example.com") }
        }
    }
}