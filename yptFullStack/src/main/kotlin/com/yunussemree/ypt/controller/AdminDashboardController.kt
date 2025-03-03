package com.yunussemree.ypt.controller

import com.yunussemree.ypt.service.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminDashboardController(
    private val categoryService: CategoryService,
    private val questionService: QuestionService,
    private val quizService: QuizService,
    private val quizAttemptService: QuizAttemptService
) {
    
    @GetMapping("/dashboard")
    fun dashboard(model: Model): String {
        // Add counts to the model
        model.addAttribute("categoryCount", categoryService.getAllCategories().size)
        model.addAttribute("questionCount", questionService.getAllQuestions().size)
        model.addAttribute("quizCount", quizService.getAllQuizzes().size)
        model.addAttribute("attemptCount", quizAttemptService.getAllQuizAttempts().size)
        
        return "admin/dashboard"
    }
    
    @GetMapping("/categories")
    fun categories(model: Model): String {
        model.addAttribute("categories", categoryService.getAllCategories())
        return "admin/categories"
    }
    
    @GetMapping("/questions")
    fun questions(model: Model): String {
        model.addAttribute("questions", questionService.getAllQuestions())
        model.addAttribute("categories", categoryService.getAllCategories())
        return "admin/questions"
    }
    
    @GetMapping("/quizzes")
    fun quizzes(model: Model): String {
        model.addAttribute("quizzes", quizService.getAllQuizzes())
        model.addAttribute("categories", categoryService.getAllCategories())
        return "admin/quizzes"
    }
    
    @GetMapping("/attempts")
    fun attempts(model: Model): String {
        model.addAttribute("attempts", quizAttemptService.getAllQuizAttempts())
        return "admin/attempts"
    }
} 