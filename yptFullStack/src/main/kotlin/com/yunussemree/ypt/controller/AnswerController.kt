package com.yunussemree.ypt.controller

import com.yunussemree.ypt.model.Answer
import com.yunussemree.ypt.service.AnswerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/answers")
class AnswerController(private val answerService: AnswerService) {

    @GetMapping
    fun getAllAnswers(): ResponseEntity<List<Answer>> {
        val answers = answerService.getAllAnswers()
        return ResponseEntity.ok(answers)
    }

    @GetMapping("/{id}")
    fun getAnswerById(@PathVariable id: Long): ResponseEntity<Answer> {
        val answer = answerService.getAnswerById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(answer)
    }
    
    @GetMapping("/question/{questionId}")
    fun getAnswersByQuestionId(@PathVariable questionId: Long): ResponseEntity<List<Answer>> {
        val answers = answerService.getAnswersByQuestionId(questionId)
        return ResponseEntity.ok(answers)
    }

    @PostMapping
    fun createAnswer(@RequestBody answer: Answer): ResponseEntity<Answer> {
        return try {
            val createdAnswer = answerService.createAnswer(answer)
            ResponseEntity.status(HttpStatus.CREATED).body(createdAnswer)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{id}")
    fun updateAnswer(
        @PathVariable id: Long,
        @RequestBody answerDetails: Answer
    ): ResponseEntity<Answer> {
        return try {
            val updatedAnswer = answerService.updateAnswer(id, answerDetails)
            ResponseEntity.ok(updatedAnswer)
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteAnswer(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            answerService.deleteAnswer(id)
            ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        }
    }
} 