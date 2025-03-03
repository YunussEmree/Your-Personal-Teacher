package com.yunussemree.ypt.controller

import com.yunussemree.ypt.model.QuizAttempt
import com.yunussemree.ypt.model.QuizResponse
import com.yunussemree.ypt.service.QuizAttemptService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.NoSuchElementException

@RestController
@RequestMapping("/api/quiz-attempts")
class QuizAttemptController(private val quizAttemptService: QuizAttemptService) {

    @GetMapping
    fun getAllQuizAttempts(): ResponseEntity<List<QuizAttempt>> {
        val attempts = quizAttemptService.getAllQuizAttempts()
        return ResponseEntity.ok(attempts)
    }

    @GetMapping("/{id}")
    fun getQuizAttemptById(@PathVariable id: Long): ResponseEntity<QuizAttempt> {
        return try {
            val attempt = quizAttemptService.getQuizAttemptById(id)
            ResponseEntity.ok(attempt)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/user/{userName}")
    fun getQuizAttemptsByUser(@PathVariable userName: String): ResponseEntity<List<QuizAttempt>> {
        val attempts = quizAttemptService.getQuizAttemptsByUser(userName)
        return ResponseEntity.ok(attempts)
    }
    
    @GetMapping("/quiz/{quizId}")
    fun getQuizAttemptsByQuiz(@PathVariable quizId: Long): ResponseEntity<List<QuizAttempt>> {
        val attempts = quizAttemptService.getQuizAttemptsByQuiz(quizId)
        return ResponseEntity.ok(attempts)
    }
    
    @GetMapping("/quiz/{quizId}/user/{userName}")
    fun getQuizAttemptsByQuizAndUser(
        @PathVariable quizId: Long,
        @PathVariable userName: String
    ): ResponseEntity<List<QuizAttempt>> {
        val attempts = quizAttemptService.getQuizAttemptsByQuizAndUser(quizId, userName)
        return ResponseEntity.ok(attempts)
    }

    @PostMapping("/start")
    fun startQuizAttempt(
        @RequestParam quizId: Long,
        @RequestParam userName: String
    ): ResponseEntity<QuizAttempt> {
        return try {
            val attempt = quizAttemptService.startQuizAttempt(quizId, userName)
            ResponseEntity.status(HttpStatus.CREATED).body(attempt)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PostMapping("/{attemptId}/submit")
    fun submitQuizResponse(
        @PathVariable attemptId: Long,
        @RequestParam questionId: Long,
        @RequestParam answerId: Long
    ): ResponseEntity<QuizResponse> {
        return try {
            val response = quizAttemptService.submitQuizResponse(attemptId, questionId, answerId)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PostMapping("/{attemptId}/complete")
    fun completeQuizAttempt(@PathVariable attemptId: Long): ResponseEntity<QuizAttempt> {
        return try {
            val completedAttempt = quizAttemptService.completeQuizAttempt(attemptId)
            ResponseEntity.ok(completedAttempt)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().build()
        }
    }
} 