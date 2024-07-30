package com.eds.dtbroker.sync.config

import com.eds.dtbroker.sync.util.PrivateKeyLoader
import com.azure.identity.DefaultAzureCredentialBuilder
import com.azure.security.keyvault.keys.KeyClient
import com.azure.security.keyvault.keys.KeyClientBuilder
import com.azure.security.keyvault.keys.models.KeyVaultKey
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
class AppConfig(
    @Value("\${spring.datasource.url}")
    private val url: String,

    @Value("\${snowflake.user}")
    private val snowflakeUser: String,

    @Value("\${snowflake.private-key-name}")
    private val snowflakePrivateKeyName: String,

    @Value("\${snowflake.private-key-password}")
    private val snowflakePrivateKeyPassword: String,

    @Value("\${spring.cloud.azure.keyvault.secret.property-sources[0].endpoint}")
    private val keyVaultEndpoint: String
) {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = connectionFactory
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        return template
    }

    @Bean
    fun keyClient(): KeyClient {
        return KeyClientBuilder()
            .vaultUrl(keyVaultEndpoint)
            .credential(DefaultAzureCredentialBuilder().build())
            .buildClient()
    }

    @Bean
    fun dataSource(keyClient: KeyClient): DataSource {
        val keyVaultKey: KeyVaultKey = keyClient.getKey(snowflakePrivateKeyName)
        val privateKeyPem = keyVaultKey.key.toString()

        val privateKeyPassword = snowflakePrivateKeyPassword  // Assuming the password is stored as a secret

        val privateKey = PrivateKeyLoader.loadPrivateKey(privateKeyPem, privateKeyPassword)
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