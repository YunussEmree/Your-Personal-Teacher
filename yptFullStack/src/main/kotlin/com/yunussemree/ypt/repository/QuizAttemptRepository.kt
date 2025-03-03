package com.yunussemree.ypt.repository

import com.yunussemree.ypt.model.QuizAttempt
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface QuizAttemptRepository : JpaRepository<QuizAttempt, Long> {
    fun findByUserName(userName: String): List<QuizAttempt>
    fun findByUserNameContainingIgnoreCase(userName: String, pageable: Pageable): Page<QuizAttempt>
    fun findByQuizId(quizId: Long): List<QuizAttempt>
    fun findByQuizId(quizId: Long, pageable: Pageable): Page<QuizAttempt>
    fun findByStartTimeBetween(startTime: LocalDateTime, endTime: LocalDateTime): List<QuizAttempt>
    fun findByStartTimeBetween(startTime: LocalDateTime, endTime: LocalDateTime, pageable: Pageable): Page<QuizAttempt>
    fun findByQuizIdAndUserName(quizId: Long, userName: String): List<QuizAttempt>
} 