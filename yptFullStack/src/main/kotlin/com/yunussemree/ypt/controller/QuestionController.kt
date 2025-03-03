package com.yunussemree.ypt.controller

import com.yunussemree.ypt.model.Question
import com.yunussemree.ypt.service.QuestionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/questions")
class QuestionController(private val questionService: QuestionService) {

    @GetMapping
    fun getAllQuestions(): ResponseEntity<List<Question>> {
        val questions = questionService.getAllQuestions()
        return ResponseEntity.ok(questions)
    }

    @GetMapping("/{id}")
    fun getQuestionById(@PathVariable id: Long): ResponseEntity<Question> {
        val question = questionService.getQuestionById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(question)
    }
    
    @GetMapping("/category/{categoryId}")
    fun getQuestionsByCategoryId(@PathVariable categoryId: Long): ResponseEntity<List<Question>> {
        val questions = questionService.getQuestionsByCategoryId(categoryId)
        return ResponseEntity.ok(questions)
    }

    @PostMapping
    fun createQuestion(@RequestBody question: Question): ResponseEntity<Question> {
        return try {
            val createdQuestion = questionService.createQuestion(question)
            ResponseEntity.status(HttpStatus.CREATED).body(createdQuestion)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{id}")
    fun updateQuestion(
        @PathVariable id: Long,
        @RequestBody questionDetails: Question
    ): ResponseEntity<Question> {
        return try {
            val updatedQuestion = questionService.updateQuestion(id, questionDetails)
            ResponseEntity.ok(updatedQuestion)
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteQuestion(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            questionService.deleteQuestion(id)
            ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        }
    }
} 