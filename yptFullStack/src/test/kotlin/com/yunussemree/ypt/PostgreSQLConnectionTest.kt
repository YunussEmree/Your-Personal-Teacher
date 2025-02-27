package com.yunussemree.ypt

import org.junit.jupiter.api.Test
import java.sql.DriverManager

class PostgreSQLConnectionTest {
    
    @Test
    fun testPostgreSQLConnection() {
        try {
            Class.forName("org.postgresql.Driver")
            val connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/ypt",
                "postgres",
                "12345"
            )
            println("PostgreSQL Connection Test: SUCCESS")
            connection.close()
        } catch (e: Exception) {
            println("PostgreSQL Connection Test: FAILED")
            e.printStackTrace()
            throw e
        }
    }
} 