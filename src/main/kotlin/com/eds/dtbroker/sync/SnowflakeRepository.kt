package com.eds.dtbroker.sync

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class SnowflakeRepository(
    val jdbcTemplate: NamedParameterJdbcTemplate,
) {
    // Get all records from a sync table
    fun getAllRecords(schema: String, table: String): List<String> {
        // TODO implement query logic
        val sql = "select * from $schema.$table;"

        // val records = jdbcTemplate.query(sql) { row, _ ->
        //     // unpack columns
        //     // convert to JSON
        // }

        // Returning an empty list of strings as a mock implementation
        return emptyList()
    }
}