package com.yunussemree.ypt.controller

import com.yunussemree.ypt.model.*
import com.yunussemree.ypt.service.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDateTime
import java.util.NoSuchElementException

@Controller
@RequestMapping("/student")
class StudentController(
    private val categoryService: CategoryService,
    private val quizService: QuizService,
    private val questionService: QuestionService,
    private val quizAttemptService: QuizAttemptService
) {

    @GetMapping("")
    fun dashboard(model: Model): String {
        // Fetch active quizzes for the student dashboard
        val activeQuizzes = quizService.getActiveQuizzes()
        model.addAttribute("quizzes", activeQuizzes)
        model.addAttribute("categories", categoryService.getAllCategories())
        model.addAttribute("activePage", "dashboard")
        return "student/dashboard"
    }

    @GetMapping("/quizzes")
    fun quizzes(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) search: String?,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        val quizzesPage = when {
            categoryId != null -> quizService.getActiveQuizzesByCategory(categoryId, pageable)
            !search.isNullOrBlank() -> quizService.searchActiveQuizzes(search, pageable)
            else -> quizService.getActiveQuizzes(pageable)
        }

        model.addAttribute("quizzes", quizzesPage.content)
        model.addAttribute("currentPage", page)
        model.addAttribute("totalPages", quizzesPage.totalPages)
        model.addAttribute("size", size)
        model.addAttribute("categoryId", categoryId)
        model.addAttribute("search", search)
        model.addAttribute("categories", categoryService.getAllCategories())
        model.addAttribute("activePage", "quizzes")
        return "student/quizzes"
    }

    @GetMapping("/quiz/{id}")
    fun viewQuiz(@PathVariable id: Long, model: Model): String {
        try {
            val quiz = quizService.getQuizById(id)
            
            // Only show active quizzes to students
            if (!quiz.isActive) {
                return "redirect:/student/quizzes"
            }
            
            model.addAttribute("quiz", quiz)
            model.addAttribute("activePage", "quizzes")
            return "student/quiz-details"
        } catch (e: NoSuchElementException) {
            return "redirect:/student/quizzes"
        }
    }

    @GetMapping("/quiz/{id}/start")
    fun startQuiz(@PathVariable id: Long, @RequestParam userName: String, model: Model): String {
        try {
            val quiz = quizService.getQuizById(id)
            
            // Only allow starting active quizzes
            if (!quiz.isActive) {
                return "redirect:/student/quizzes"
            }
            
            // Create a new quiz attempt
            val attempt = QuizAttempt(
                quiz = quiz,
                userName = userName,
                startTime = LocalDateTime.now(),
                isCompleted = false
            )
            
            val savedAttempt = quizAttemptService.saveAttempt(attempt)
            
            model.addAttribute("quiz", quiz)
            model.addAttribute("attempt", savedAttempt)
            model.addAttribute("currentQuestionIndex", 0)
            model.addAttribute("activePage", "quizzes")
            return "student/take-quiz"
        } catch (e: NoSuchElementException) {
            return "redirect:/student/quizzes"
        }
    }

    @PostMapping("/quiz/submit-answer")
    fun submitAnswer(
        @RequestParam attemptId: Long,
        @RequestParam questionId: Long,
        @RequestParam answerId: Long,
        @RequestParam currentQuestionIndex: Int,
        model: Model
    ): String {
        try {
            val attempt = quizAttemptService.getAttemptById(attemptId)
            val question = questionService.getQuestionById(questionId)
            val answer = question.answers?.find { it.id == answerId }
            
            if (answer != null) {
                // Save the response using the proper method
                quizAttemptService.submitQuizResponse(attemptId, questionId, answerId)
                
                // Prepare for the next question
                val quiz = attempt.quiz
                val totalQuestions = quiz.questions?.size ?: 0
                val nextQuestionIndex = currentQuestionIndex + 1
                
                // Check if this is the last question
                if (nextQuestionIndex >= totalQuestions) {
                    // Complete the quiz
                    quizAttemptService.completeQuizAttempt(attemptId)
                    return "redirect:/student/quiz/${quiz.id}/result?attemptId=$attemptId"
                }
                
                model.addAttribute("quiz", quiz)
                model.addAttribute("attempt", attempt)
                model.addAttribute("currentQuestionIndex", nextQuestionIndex)
                model.addAttribute("activePage", "quizzes")
                return "student/take-quiz"
            } else {
                // Handle invalid answer
                return "redirect:/student/dashboard"
            }
        } catch (e: NoSuchElementException) {
            return "redirect:/student/dashboard"
        }
    }

    @GetMapping("/quiz/{quizId}/result")
    fun quizResult(@PathVariable quizId: Long, @RequestParam attemptId: Long, model: Model): String {
        try {
            val attempt = quizAttemptService.getAttemptById(attemptId)
            val responses = attempt.responses ?: emptyList()
            
            // Calculate statistics
            val totalQuestions = attempt.quiz.questions?.size ?: 0
            val correctCount = responses.count { it.isCorrect }
            val scorePercentage = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0
            
            model.addAttribute("attempt", attempt)
            model.addAttribute("responses", responses)
            model.addAttribute("totalQuestions", totalQuestions)
            model.addAttribute("correctCount", correctCount)
            model.addAttribute("scorePercentage", scorePercentage)
            model.addAttribute("activePage", "quizzes")
            return "student/quiz-result"
        } catch (e: NoSuchElementException) {
            return "redirect:/student/dashboard"
        }
    }

    @GetMapping("/history")
    fun quizHistory(
        @RequestParam userName: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, size, Sort.by("startTime").descending())
        val attemptsPage = quizAttemptService.getAttemptsByUsername(userName, pageable)
        
        model.addAttribute("attempts", attemptsPage.content)
        model.addAttribute("currentPage", page)
        model.addAttribute("totalPages", attemptsPage.totalPages)
        model.addAttribute("size", size)
        model.addAttribute("userName", userName)
        model.addAttribute("activePage", "history")
        return "student/history"
    }
} 