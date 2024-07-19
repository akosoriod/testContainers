package com.eds.dtbroker.sync

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class SnowflakeRepository(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    private val jdbcTemplate: JdbcTemplate,
    private val objectMapper: ObjectMapper
) {

    @Value("\${snowflake.warehouse}")
    private lateinit var warehouse: String

    @Value("\${snowflake.db}")
    private lateinit var database: String

    // Get all records from a sync table
    fun getAllRecords(schema: String, table: String): List<String> {
        val selectSql = "SELECT * FROM $schema.$table;"
        return namedParameterJdbcTemplate.query(selectSql) { rs, _ -> mapRowToJsonString(rs) }
    }

    private fun mapRowToJsonString(rs: ResultSet): String {
        val metaData = rs.metaData
        val columnCount = metaData.columnCount
        val jsonNode = objectMapper.createObjectNode()

        for (i in 1..columnCount) {
            val columnName = metaData.getColumnName(i)
            val columnValue = rs.getObject(i)
            jsonNode.putPOJO(columnName, columnValue)
        }

        return jsonNode.toString()
    }
}