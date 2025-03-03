package com.yunussemree.ypt.controller

import com.yunussemree.ypt.model.Quiz
import com.yunussemree.ypt.service.QuizService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/quizzes")
class QuizController(private val quizService: QuizService) {

    @GetMapping
    fun getAllQuizzes(): ResponseEntity<List<Quiz>> {
        val quizzes = quizService.getAllQuizzes()
        return ResponseEntity.ok(quizzes)
    }
    
    @GetMapping("/active")
    fun getActiveQuizzes(): ResponseEntity<List<Quiz>> {
        val quizzes = quizService.getActiveQuizzes()
        return ResponseEntity.ok(quizzes)
    }

    @GetMapping("/{id}")
    fun getQuizById(@PathVariable id: Long): ResponseEntity<Quiz> {
        val quiz = quizService.getQuizById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(quiz)
    }
    
    @GetMapping("/category/{categoryId}")
    fun getQuizzesByCategory(@PathVariable categoryId: Long): ResponseEntity<List<Quiz>> {
        val quizzes = quizService.getQuizzesByCategory(categoryId)
        return ResponseEntity.ok(quizzes)
    }
    
    @GetMapping("/search")
    fun searchQuizzesByTitle(@RequestParam title: String): ResponseEntity<List<Quiz>> {
        val quizzes = quizService.searchQuizzesByTitle(title)
        return ResponseEntity.ok(quizzes)
    }

    @PostMapping
    fun createQuiz(@RequestBody quiz: Quiz): ResponseEntity<Quiz> {
        return try {
            val createdQuiz = quizService.createQuiz(quiz)
            ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{id}")
    fun updateQuiz(
        @PathVariable id: Long,
        @RequestBody quizDetails: Quiz
    ): ResponseEntity<Quiz> {
        return try {
            val updatedQuiz = quizService.updateQuiz(id, quizDetails)
            ResponseEntity.ok(updatedQuiz)
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping("/{id}/questions")
    fun addQuestionsToQuiz(
        @PathVariable id: Long,
        @RequestBody questionIds: List<Long>
    ): ResponseEntity<Quiz> {
        return try {
            val updatedQuiz = quizService.addQuestionsToQuiz(id, questionIds)
            ResponseEntity.ok(updatedQuiz)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @DeleteMapping("/{id}/questions")
    fun removeQuestionsFromQuiz(
        @PathVariable id: Long,
        @RequestBody questionIds: List<Long>
    ): ResponseEntity<Quiz> {
        return try {
            val updatedQuiz = quizService.removeQuestionsFromQuiz(id, questionIds)
            ResponseEntity.ok(updatedQuiz)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteQuiz(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            quizService.deleteQuiz(id)
            ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        }
    }
} 