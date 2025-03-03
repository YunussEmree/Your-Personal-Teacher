package com.yunussemree.ypt.service

import com.yunussemree.ypt.model.Quiz
import com.yunussemree.ypt.model.Question
import com.yunussemree.ypt.repository.QuizRepository
import com.yunussemree.ypt.repository.CategoryRepository
import com.yunussemree.ypt.repository.QuestionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val categoryRepository: CategoryRepository,
    private val questionRepository: QuestionRepository
) {
    
    fun getAllQuizzes(): List<Quiz> = quizRepository.findAll()
    
    fun getActiveQuizzes(): List<Quiz> = quizRepository.findByIsActiveTrue()
    
    fun getQuizById(id: Long): Quiz? = quizRepository.findById(id).orElse(null)
    
    fun getQuizzesByCategory(categoryId: Long): List<Quiz> = quizRepository.findByCategoryId(categoryId)
    
    fun searchQuizzesByTitle(title: String): List<Quiz> = quizRepository.findByTitleContainingIgnoreCase(title)
    
    @Transactional
    fun createQuiz(quiz: Quiz): Quiz {
        // Set creation time
        quiz.createdAt = LocalDateTime.now()
        
        // Validate category if provided
        if (quiz.category != null && quiz.category?.id != 0L) {
            val categoryId = quiz.category?.id
            val category = categoryId?.let {
                categoryRepository.findById(it).orElseThrow {
                    RuntimeException("Category not found with id: $categoryId")
                }
            }
            quiz.category = category
        }
        
        // Save the quiz first without questions
        val savedQuiz = quizRepository.save(quiz)
        
        // Process questions if any
        if (quiz.questions.isNotEmpty()) {
            val questionIds = quiz.questions.map { it.id }
            val validQuestions = questionRepository.findAllById(questionIds).toSet()
            
            if (validQuestions.size != quiz.questions.size) {
                throw RuntimeException("Some of the provided questions do not exist")
            }
            
            savedQuiz.questions = validQuestions.toMutableSet()
            return quizRepository.save(savedQuiz)
        }
        
        return savedQuiz
    }
    
    @Transactional
    fun updateQuiz(id: Long, quizDetails: Quiz): Quiz {
        val quiz = getQuizById(id) ?: throw RuntimeException("Quiz not found with id: $id")
        
        // Update basic fields
        quiz.title = quizDetails.title
        quiz.description = quizDetails.description
        quiz.timeLimit = quizDetails.timeLimit
        quiz.isActive = quizDetails.isActive
        
        // Update category if provided
        if (quizDetails.category != null && quizDetails.category?.id != 0L && 
            quizDetails.category?.id != quiz.category?.id) {
            val categoryId = quizDetails.category?.id
            val category = categoryId?.let {
                categoryRepository.findById(it).orElseThrow {
                    RuntimeException("Category not found with id: $categoryId")
                }
            }
            quiz.category = category
        }
        
        // Update questions if provided
        if (quizDetails.questions.isNotEmpty()) {
            val questionIds = quizDetails.questions.map { it.id }
            val validQuestions = questionRepository.findAllById(questionIds).toSet()
            
            if (validQuestions.size != quizDetails.questions.size) {
                throw RuntimeException("Some of the provided questions do not exist")
            }
            
            quiz.questions = validQuestions.toMutableSet()
        }
        
        return quizRepository.save(quiz)
    }
    
    @Transactional
    fun addQuestionsToQuiz(quizId: Long, questionIds: List<Long>): Quiz {
        val quiz = getQuizById(quizId) ?: throw RuntimeException("Quiz not found with id: $quizId")
        
        // Find valid questions
        val newQuestions = questionRepository.findAllById(questionIds)
        
        if (newQuestions.size != questionIds.size) {
            throw RuntimeException("Some of the provided questions do not exist")
        }
        
        // Add questions to the quiz
        quiz.questions.addAll(newQuestions)
        
        return quizRepository.save(quiz)
    }
    
    @Transactional
    fun removeQuestionsFromQuiz(quizId: Long, questionIds: List<Long>): Quiz {
        val quiz = getQuizById(quizId) ?: throw RuntimeException("Quiz not found with id: $quizId")
        
        // Remove questions
        quiz.questions.removeIf { it.id in questionIds }
        
        return quizRepository.save(quiz)
    }
    
    @Transactional
    fun deleteQuiz(id: Long) {
        val quiz = getQuizById(id) ?: throw RuntimeException("Quiz not found with id: $id")
        quizRepository.delete(quiz)
    }
} 