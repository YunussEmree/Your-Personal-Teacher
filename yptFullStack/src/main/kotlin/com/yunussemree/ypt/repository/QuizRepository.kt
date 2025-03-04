package com.yunussemree.ypt.repository

import com.yunussemree.ypt.model.Quiz
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuizRepository : JpaRepository<Quiz, Long> {
    fun findByTitleContainingIgnoreCase(title: String): List<Quiz>
    fun findByTitleContainingIgnoreCase(title: String, pageable: Pageable): Page<Quiz>
    fun findByCategoryId(categoryId: Long): List<Quiz>
    fun findByCategoryId(categoryId: Long, pageable: Pageable): Page<Quiz>
    fun findByIsActiveTrue(): List<Quiz>
    fun findByIsActiveTrue(pageable: Pageable): Page<Quiz>
    fun findByCategoryIdAndIsActiveTrue(categoryId: Long, pageable: Pageable): Page<Quiz>
    fun findByTitleContainingIgnoreCaseAndIsActiveTrue(title: String, pageable: Pageable): Page<Quiz>
    fun findByQuestionsId(questionId: Long): List<Quiz>
} 