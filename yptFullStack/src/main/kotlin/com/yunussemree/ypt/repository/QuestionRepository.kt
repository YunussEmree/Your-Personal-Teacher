package com.yunussemree.ypt.repository

import com.yunussemree.ypt.model.Question
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository : JpaRepository<Question, Long> {
    fun findByCategoryId(categoryId: Long): List<Question>
    fun findByCategoryId(categoryId: Long, pageable: Pageable): Page<Question>
    fun findByTextContainingIgnoreCase(text: String): List<Question>
    fun findByTextContainingIgnoreCase(text: String, pageable: Pageable): Page<Question>
} 