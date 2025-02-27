package com.yunussemree.ypt.repository

import com.yunussemree.ypt.model.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository : JpaRepository<Question, Long> {
    fun findByCategoryId(categoryId: Long): List<Question>
} 