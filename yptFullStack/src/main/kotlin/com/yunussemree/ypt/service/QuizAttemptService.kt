package com.yunussemree.ypt.service

import com.yunussemree.ypt.model.*
import com.yunussemree.ypt.repository.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class QuizAttemptService(
    private val quizRepository: QuizRepository,
    private val quizAttemptRepository: QuizAttemptRepository,
    private val questionRepository: QuestionRepository,
    private val answerRepository: AnswerRepository,
    private val quizResponseRepository: QuizResponseRepository
) {
    
    fun getAllAttempts(): List<QuizAttempt> {
        return quizAttemptRepository.findAll()
    }
    
    fun getAllAttempts(pageable: Pageable): Page<QuizAttempt> {
        return quizAttemptRepository.findAll(pageable)
    }
    
    fun getAttemptById(id: Long): QuizAttempt {
        return quizAttemptRepository.findById(id).orElseThrow { NoSuchElementException("Quiz attempt not found with id: $id") }
    }
    
    fun getQuizAttemptById(id: Long): QuizAttempt {
        return quizAttemptRepository.findById(id).orElseThrow { NoSuchElementException("Quiz attempt not found with id: $id") }
    }
    
    fun getAttemptsByQuiz(quizId: Long, pageable: Pageable): Page<QuizAttempt> {
        return quizAttemptRepository.findByQuizId(quizId, pageable)
    }
    
    fun getAttemptsByUsername(username: String, pageable: Pageable): Page<QuizAttempt> {
        val lowerUsername = username.lowercase()
        return quizAttemptRepository.findByUsernameContainingIgnoreCase(lowerUsername, pageable)
    }
    
    fun getAttemptsByDate(date: LocalDate, pageable: Pageable): Page<QuizAttempt> {
        val startOfDay = LocalDateTime.of(date, LocalTime.MIN)
        val endOfDay = LocalDateTime.of(date, LocalTime.MAX)
        return quizAttemptRepository.findByStartTimeBetween(startOfDay, endOfDay, pageable)
    }
    
    fun saveAttempt(attempt: QuizAttempt): QuizAttempt {
        return quizAttemptRepository.save(attempt)
    }
    
    fun saveResponse(response: QuizResponse): QuizResponse {
        return quizResponseRepository.save(response)
    }
    
    @Transactional
    fun deleteAttempt(id: Long) {
        val attempt = getAttemptById(id)
        quizAttemptRepository.delete(attempt)
    }
    
    fun countAttempts(): Long {
        return quizAttemptRepository.count()
    }
    
    fun getAllQuizAttempts(): List<QuizAttempt> = quizAttemptRepository.findAll()
    
    fun getQuizAttemptsByUser(userName: String): List<QuizAttempt> = quizAttemptRepository.findByUserName(userName)
    
    fun getQuizAttemptsByQuiz(quizId: Long): List<QuizAttempt> = quizAttemptRepository.findByQuizId(quizId)
    
    fun getQuizAttemptsByQuizAndUser(quizId: Long, userName: String): List<QuizAttempt> {
        val quizAttempts = quizAttemptRepository.findByQuizId(quizId)
        return quizAttempts.filter { it.userName == userName }
    }
    
    @Transactional
    fun startQuizAttempt(quizId: Long, userName: String): QuizAttempt {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { RuntimeException("Quiz not found with id: $quizId") }
        
        if (!quiz.isActive) {
            throw RuntimeException("This quiz is not active")
        }
        
        if (quiz.questions.isEmpty()) {
            throw RuntimeException("This quiz has no questions")
        }
        
        val quizAttempt = QuizAttempt(
            quiz = quiz,
            userName = userName,
            startTime = LocalDateTime.now(),
            isCompleted = false
        )
        
        return quizAttemptRepository.save(quizAttempt)
    }
    
    @Transactional
    fun submitQuizResponse(attemptId: Long, questionId: Long, answerId: Long): QuizResponse {
        val quizAttempt = quizAttemptRepository.findById(attemptId)
            .orElseThrow { RuntimeException("Quiz attempt not found with id: $attemptId") }
        
        if (quizAttempt.isCompleted) {
            throw RuntimeException("This quiz attempt is already completed")
        }
        
        val question = questionRepository.findById(questionId)
            .orElseThrow { RuntimeException("Question not found with id: $questionId") }
        
        val answer = answerRepository.findById(answerId)
            .orElseThrow { RuntimeException("Answer not found with id: $answerId") }
        
        // Verify that this question belongs to the quiz
        if (question !in quizAttempt.quiz.questions) {
            throw RuntimeException("This question is not part of the quiz")
        }
        
        // Verify that this answer belongs to the question
        if (answer.question.id != question.id) {
            throw RuntimeException("This answer does not belong to the question")
        }
        
        // Check if response for this question already exists
        val existingResponse = quizAttempt.responses.find { it.question.id == questionId }
        if (existingResponse != null) {
            // Update existing response
            existingResponse.selectedAnswer = answer
            existingResponse.isCorrect = answer.isCorrect
            return existingResponse
        }
        
        // Create new response
        val quizResponse = QuizResponse(
            quizAttempt = quizAttempt,
            question = question,
            selectedAnswer = answer,
            isCorrect = answer.isCorrect
        )
        
        quizAttempt.responses.add(quizResponse)
        quizAttemptRepository.save(quizAttempt)
        
        return quizResponse
    }
    
    @Transactional
    fun completeQuizAttempt(attemptId: Long): QuizAttempt {
        val quizAttempt = quizAttemptRepository.findById(attemptId)
            .orElseThrow { RuntimeException("Quiz attempt not found with id: $attemptId") }
        
        if (quizAttempt.isCompleted) {
            throw RuntimeException("This quiz attempt is already completed")
        }
        
        // Set end time and status
        quizAttempt.endTime = LocalDateTime.now()
        quizAttempt.isCompleted = true
        
        // Calculate score
        val correctAnswers = quizAttempt.responses.count { it.isCorrect }
        val totalQuestions = quizAttempt.quiz.questions.size
        
        // Score as percentage
        quizAttempt.score = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0
        
        return quizAttemptRepository.save(quizAttempt)
    }
} 