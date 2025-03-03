package com.yunussemree.ypt.repository

import com.yunussemree.ypt.model.QuizAttempt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuizAttemptRepository : JpaRepository<QuizAttempt, Long> {
    fun findByUserName(userName: String): List<QuizAttempt>
    fun findByQuizIdAndUserName(quizId: Long, userName: String): List<QuizAttempt>
    fun findByQuizId(quizId: Long): List<QuizAttempt>
} 