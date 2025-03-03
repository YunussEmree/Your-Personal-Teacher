package com.yunussemree.ypt.service

import com.yunussemree.ypt.model.Question
import com.yunussemree.ypt.repository.QuestionRepository
import com.yunussemree.ypt.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val categoryRepository: CategoryRepository
) {
    
    fun getAllQuestions(): List<Question> = questionRepository.findAll()
    
    fun getQuestionById(id: Long): Question? = questionRepository.findById(id).orElse(null)
    
    fun getQuestionsByCategoryId(categoryId: Long): List<Question> = questionRepository.findByCategoryId(categoryId)
    
    fun createQuestion(question: Question): Question {
        // Ensure category exists
        val categoryId = question.category.id
        val category = categoryRepository.findById(categoryId)
            .orElseThrow { RuntimeException("Category not found with id: $categoryId") }
        
        question.category = category
        return questionRepository.save(question)
    }
    
    fun updateQuestion(id: Long, questionDetails: Question): Question {
        val question = getQuestionById(id) ?: throw RuntimeException("Question not found with id: $id")
        
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
    
    fun deleteQuestion(id: Long) {
        val question = getQuestionById(id) ?: throw RuntimeException("Question not found with id: $id")
        questionRepository.delete(question)
    }
} 