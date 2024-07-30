package com.eds.dtbroker.sync.config

import com.eds.dtbroker.sync.util.PrivateKeyLoader
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.util.*
import javax.sql.DataSource

@Configuration
class AppConfig {

    @Value("\${spring.datasource.url}")
    private lateinit var url: String

    @Value("\${spring.datasource.username}")
    private lateinit var username: String

    //TODO: Read private key contents and password from vault and remove these variables.
    @Value("\${spring.datasource.private-key-path}")
    private lateinit var privateKeyPath: String

    @Value("\${spring.datasource.private-key-password}")
    private lateinit var privateKeyPassword: String

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        return template
    }

    @Bean
    fun dataSource(): DataSource {
        //TODO: Read private key contents and password from vault
        val privateKey = PrivateKeyLoader.loadPrivateKey(privateKeyPath, privateKeyPassword)
        val properties = Properties()
        properties["user"] = username
        properties["privateKey"] = privateKey

        val driverManagerDataSource = DriverManagerDataSource(url, properties)
        val config = HikariConfig()
        config.dataSource = driverManagerDataSource

        return HikariDataSource(config)
    }

    @Bean
    fun namedParameterJdbcTemplate(databrokerDS: DataSource): NamedParameterJdbcTemplate{
        return NamedParameterJdbcTemplate(databrokerDS)
    }
}