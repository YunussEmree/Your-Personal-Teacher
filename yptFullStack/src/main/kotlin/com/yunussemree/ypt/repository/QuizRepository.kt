package com.yunussemree.ypt.repository

import com.yunussemree.ypt.model.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuizRepository : JpaRepository<Quiz, Long> {
    fun findByCategoryId(categoryId: Long): List<Quiz>
    fun findByIsActiveTrue(): List<Quiz>
    fun findByTitleContainingIgnoreCase(title: String): List<Quiz>
} 