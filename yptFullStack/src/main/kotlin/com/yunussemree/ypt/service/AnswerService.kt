package com.yunussemree.ypt.service

import com.yunussemree.ypt.model.Answer
import com.yunussemree.ypt.repository.AnswerRepository
import com.yunussemree.ypt.repository.QuestionRepository
import org.springframework.stereotype.Service

@Service
class AnswerService(
    private val answerRepository: AnswerRepository,
    private val questionRepository: QuestionRepository
) {
    
    fun getAllAnswers(): List<Answer> = answerRepository.findAll()
    
    fun getAnswerById(id: Long): Answer? = answerRepository.findById(id).orElse(null)
    
    fun getAnswersByQuestionId(questionId: Long): List<Answer> = answerRepository.findByQuestionId(questionId)
    
    fun createAnswer(answer: Answer): Answer {
        // Ensure question exists
        val questionId = answer.question.id
        val question = questionRepository.findById(questionId)
            .orElseThrow { RuntimeException("Question not found with id: $questionId") }
        
        answer.question = question
        return answerRepository.save(answer)
    }
    
    fun updateAnswer(id: Long, answerDetails: Answer): Answer {
        val answer = getAnswerById(id) ?: throw RuntimeException("Answer not found with id: $id")
        
        // Update fields
        answer.text = answerDetails.text
        answer.isCorrect = answerDetails.isCorrect
        
        // Update question if it has changed
        if (answerDetails.question.id != answer.question.id) {
            val newQuestion = questionRepository.findById(answerDetails.question.id)
                .orElseThrow { RuntimeException("Question not found with id: ${answerDetails.question.id}") }
            answer.question = newQuestion
        }
        
        return answerRepository.save(answer)
    }
    
    fun deleteAnswer(id: Long) {
        val answer = getAnswerById(id) ?: throw RuntimeException("Answer not found with id: $id")
        answerRepository.delete(answer)
    }
} 