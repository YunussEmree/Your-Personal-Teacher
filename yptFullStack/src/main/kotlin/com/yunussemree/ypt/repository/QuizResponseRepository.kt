package com.yunussemree.ypt.repository

import com.yunussemree.ypt.model.QuizResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuizResponseRepository : JpaRepository<QuizResponse, Long> {
    fun findByQuizAttemptId(quizAttemptId: Long): List<QuizResponse>
} 