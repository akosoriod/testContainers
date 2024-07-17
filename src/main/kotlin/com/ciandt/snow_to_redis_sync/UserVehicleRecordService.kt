package com.ciandt.snow_to_redis_sync

import io.lettuce.core.RedisClient
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.snowflake.client.jdbc.SnowflakeDriver
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

@Service
class UserVehicleRecordService(
    @Value("\${snowflake.accountName}") private val accountName: String,
    @Value("\${snowflake.dbName}") private val dbName: String,
    @Value("\${snowflake.schemaName:PUBLIC}") private val schemaName: String,
    @Value("\${snowflake.warehouseName}") private val whName: String,
    @Value("\${snowflake.roleName}") private val roleName: String,
    @Value("\${snowflake.username}") private val user: String,
    @Value("\${snowflake.password}") private val password: String,
    @Value("\${snowflake.tableName}") private val tableName: String,
    private val redisClient: RedisClient
) {
    private val logger = LoggerFactory.getLogger(UserVehicleRecordService::class.java)

    fun syncData() {
        try {
            DriverManager.setLoginTimeout(30)

            val jdbcUrl = "jdbc:snowflake://$accountName.snowflakecomputing.com/?db=$dbName&schema=$schemaName&warehouse=$whName&role=$roleName"

            val connectionProperties = Properties().apply {
                put("user", user)
                put("password", password)
            }

            logger.info("Connecting to Snowflake using this URL -> $jdbcUrl")
            DriverManager.registerDriver(SnowflakeDriver())

            DriverManager.getConnection(jdbcUrl, connectionProperties).use { connection ->
                logger.info("Connection successful!")

                setupDatabase(connection, dbName, schemaName)

                redisClient.connect().use { redisConnection ->
                    val redisCommands = redisConnection.sync()

                    val sql = "SELECT * FROM $schemaName.$tableName"
                    logger.info("Executing query: $sql")

                    connection.createStatement().use { statement ->
                        statement.executeQuery(sql).use { resultSet ->
                            var count = 0
                            redisCommands.multi() // Start a transaction

                            while (resultSet.next()) {
                                try {
                                    val record = resultSet.toUserVehicleRecord()
                                    val json = Json.encodeToString(record)
                                    val redisKey = "${record.vin}"
                                    redisCommands.set(redisKey, json)
                                    count++
                                } catch (e: Exception) {
                                    logger.error("Error processing record: ${e.message}", e)
                                }
                            }

                            redisCommands.exec() // Execute the transaction
                            logger.info("Inserted $count records into Redis")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("An error occurred: ${e.message}", e)
        }
    }

    fun getRedisInfo(): String {
        logger.info("Fetching Redis info")
        redisClient.connect().use { redisConnection ->
            val redisCommands = redisConnection.sync()
            return redisCommands.info()
        }
    }

    fun flushAll(): String {
        logger.info("Flushing all data from Redis")
        redisClient.connect().use { redisConnection ->
            val redisCommands = redisConnection.sync()
            redisCommands.flushall()
            return "All data flushed successfully"
        }
    }

    private fun setupDatabase(connection: Connection, dbName: String, schemaName: String) {
        connection.createStatement().use { statement ->
            statement.execute("USE DATABASE $dbName;")
            statement.execute("USE SCHEMA $schemaName;")
        }
    }

    private fun ResultSet.toUserVehicleRecord(): UserVehicleRecord {
        return UserVehicleRecord(
            userId = getLong("USER_ID"),
            phone = getString("PHONE"),
            firstName = getString("FIRST_NAME"),
            lastName = getString("LAST_NAME"),
            email = getString("EMAIL"),
            vin = getLong("VIN"),
            color = getString("COLOR"),
            year = getInt("YEAR"),
            make = getString("MAKE"),
            model = getString("MODEL"),
            trim = getString("TRIM")
        )
    }

    @Serializable
    data class UserVehicleRecord(
        val userId: Long,
        val phone: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val vin: Long,
        val color: String,
        val year: Int,
        val make: String,
        val model: String,
        val trim: String
    )
}