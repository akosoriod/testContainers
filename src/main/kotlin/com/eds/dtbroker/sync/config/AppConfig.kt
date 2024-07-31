package com.eds.dtbroker.sync.config

import com.eds.dtbroker.sync.util.PrivateKeyLoader
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.util.*
import javax.sql.DataSource

@Configuration
class AppConfig(
    @Value("\${redisAccessKey}")
    private val redisAccessKeyVault: String,

    @Value("\${spring.datasource.url}")
    private val url: String,

    @Value("\${snowflake.user}")
    private val snowflakeUser: String,

    @Value("\${snowflakePrivateKey}")
    private val snowflakePrivateKeyName: String,

    @Value("\${snowflakePrivateKeyPassword}")
    private val snowflakePrivateKeyPassword: String
) {

    @Bean
    fun dataSource(): DataSource {
        val privateKey = PrivateKeyLoader.loadPrivateKey(snowflakePrivateKeyName, snowflakePrivateKeyPassword)
        val properties = Properties().apply {
            put("user", snowflakeUser)
            put("privateKey", privateKey)
        }

        val driverManagerDataSource = DriverManagerDataSource(url, properties)
        val config = HikariConfig().apply {
            dataSource = driverManagerDataSource
        }

        return HikariDataSource(config)
    }

    @Bean
    fun namedParameterJdbcTemplate(databrokerDS: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(databrokerDS)
    }
}