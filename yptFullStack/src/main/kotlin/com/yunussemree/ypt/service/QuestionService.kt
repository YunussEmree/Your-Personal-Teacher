package com.yunussemree.ypt.service

import com.yunussemree.ypt.model.Question
import com.yunussemree.ypt.repository.QuestionRepository
import com.yunussemree.ypt.repository.CategoryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val categoryRepository: CategoryRepository
) {
    
    fun getAllQuestions(): List<Question> = questionRepository.findAll()
    
    fun getAllQuestions(pageable: Pageable): Page<Question> = questionRepository.findAll(pageable)
    
    fun getQuestionById(id: Long): Question = questionRepository.findById(id).orElseThrow { NoSuchElementException("Question not found with id: $id") }
    
    fun getQuestionsByIds(ids: List<Long>): List<Question> = questionRepository.findAllById(ids)
    
    fun getQuestionsByCategory(categoryId: Long, pageable: Pageable): Page<Question> = questionRepository.findByCategoryId(categoryId, pageable)
    
    fun getQuestionsByCategoryId(categoryId: Long): List<Question> = questionRepository.findByCategoryId(categoryId)
    
    fun searchQuestions(query: String, pageable: Pageable): Page<Question> = questionRepository.findByTextContainingIgnoreCase(query, pageable)
    
    fun createQuestion(question: Question): Question {
        // Ensure category exists
        val categoryId = question.category.id
        val category = categoryRepository.findById(categoryId)
            .orElseThrow { RuntimeException("Category not found with id: $categoryId") }
        
        question.category = category
        return questionRepository.save(question)
    }
    
    fun updateQuestion(id: Long, questionDetails: Question): Question {
        val question = getQuestionById(id)
        
        // Update fields
        question.text = questionDetails.text
        question.difficulty = questionDetails.difficulty
        
        // Update category if it has changed
        if (questionDetails.category.id != question.category.id) {
            val newCategory = categoryRepository.findById(questionDetails.category.id)
                .orElseThrow { RuntimeException("Category not found with id: ${questionDetails.category.id}") }
            question.category = newCategory
        }
        
        return questionRepository.save(question)
    }
    
    @Transactional
    fun deleteQuestion(id: Long) {
        val question = getQuestionById(id)
        questionRepository.delete(question)
    }

    fun countQuestions(): Long = questionRepository.count()
} 