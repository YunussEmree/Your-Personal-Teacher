package com.yunussemree.ypt.controller

import com.yunussemree.ypt.model.*
import com.yunussemree.ypt.service.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.NoSuchElementException

@Controller
@RequestMapping("/admin")
class AdminDashboardController(
    private val categoryService: CategoryService,
    private val questionService: QuestionService,
    private val quizService: QuizService,
    private val quizAttemptService: QuizAttemptService
) {

    @GetMapping("")
    fun dashboard(model: Model): String {
        val categoryCount = categoryService.countCategories()
        val questionCount = questionService.countQuestions()
        val quizCount = quizService.countQuizzes()
        val attemptCount = quizAttemptService.countAttempts()

        model.addAttribute("categoryCount", categoryCount)
        model.addAttribute("questionCount", questionCount)
        model.addAttribute("quizCount", quizCount)
        model.addAttribute("attemptCount", attemptCount)

        return "admin/dashboard"
    }

    // Category Management
    @GetMapping("/categories")
    fun categories(model: Model): String {
        val categories = categoryService.getAllCategories()
        model.addAttribute("categories", categories)
        return "admin/categories"
    }

    @GetMapping("/categories/new")
    fun newCategory(model: Model): String {
        model.addAttribute("category", Category())
        return "admin/category-form"
    }

    @PostMapping("/categories/save")
    fun saveCategory(@ModelAttribute category: Category, redirectAttributes: RedirectAttributes): String {
        categoryService.createCategory(category)
        redirectAttributes.addFlashAttribute("message", "Category saved successfully")
        return "redirect:/admin/categories"
    }

    @GetMapping("/categories/edit/{id}")
    fun editCategory(@PathVariable id: Long, model: Model): String {
        val category = categoryService.getCategoryById(id)
        model.addAttribute("category", category)
        return "admin/category-form"
    }

    @PostMapping("/categories/update/{id}")
    fun updateCategory(@PathVariable id: Long, @ModelAttribute category: Category, redirectAttributes: RedirectAttributes): String {
        val updatedCategory = category.copy(id = id)
        categoryService.createCategory(updatedCategory)
        redirectAttributes.addFlashAttribute("message", "Category updated successfully")
        return "redirect:/admin/categories"
    }

    @GetMapping("/categories/view/{id}")
    fun viewCategory(@PathVariable id: Long, model: Model): String {
        val category = categoryService.getCategoryById(id)
        model.addAttribute("category", category)
        return "admin/category-view"
    }

    @PostMapping("/categories/delete/{id}")
    fun deleteCategory(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        categoryService.deleteCategory(id)
        redirectAttributes.addFlashAttribute("message", "Category deleted successfully")
        return "redirect:/admin/categories"
    }

    // Question Management
    @GetMapping("/questions")
    fun questions(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) search: String?,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, size, Sort.by("id").descending())
        val questionsPage = when {
            categoryId != null -> questionService.getQuestionsByCategory(categoryId, pageable)
            !search.isNullOrBlank() -> questionService.searchQuestions(search, pageable)
            else -> questionService.getAllQuestions(pageable)
        }

        model.addAttribute("questions", questionsPage.content)
        model.addAttribute("currentPage", page)
        model.addAttribute("totalPages", questionsPage.totalPages)
        model.addAttribute("size", size)
        model.addAttribute("categoryId", categoryId)
        model.addAttribute("search", search)
        model.addAttribute("categories", categoryService.getAllCategories())

        return "admin/questions"
    }

    @GetMapping("/questions/new")
    fun newQuestion(model: Model): String {
        val question = Question()
        model.addAttribute("question", question)
        model.addAttribute("categories", categoryService.getAllCategories())
        return "admin/question-form"
    }

    @PostMapping("/questions/save")
    fun saveQuestion(
        @ModelAttribute question: Question,
        @RequestParam categoryId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val category = categoryService.getCategoryById(id = categoryId)
        question.category = category
        
        // Ensure at least one answer is marked as correct
        val hasCorrectAnswer = question.answers?.any { it.isCorrect } ?: false
        if (!hasCorrectAnswer) {
            redirectAttributes.addFlashAttribute("error", "At least one answer must be marked as correct")
            return "redirect:/admin/questions/new"
        }
        
        questionService.createQuestion(question)
        redirectAttributes.addFlashAttribute("message", "Question saved successfully")
        return "redirect:/admin/questions"
    }

    @GetMapping("/questions/edit/{id}")
    fun editQuestion(@PathVariable id: Long, model: Model): String {
        val question = questionService.getQuestionById(id)
        model.addAttribute("question", question)
        model.addAttribute("categories", categoryService.getAllCategories())
        return "admin/question-form"
    }

    @PostMapping("/questions/update/{id}")
    fun updateQuestion(
        @PathVariable id: Long,
        @ModelAttribute question: Question,
        @RequestParam categoryId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        val category = categoryService.getCategoryById(categoryId)
        val updatedQuestion = question.copy(id = id, category = category)
        
        // Ensure at least one answer is marked as correct
        val hasCorrectAnswer = updatedQuestion.answers?.any { it.isCorrect } ?: false
        if (!hasCorrectAnswer) {
            redirectAttributes.addFlashAttribute("error", "At least one answer must be marked as correct")
            return "redirect:/admin/questions/edit/$id"
        }
        
        questionService.updateQuestion(id, updatedQuestion)
        redirectAttributes.addFlashAttribute("message", "Question updated successfully")
        return "redirect:/admin/questions"
    }

    @GetMapping("/questions/view/{id}")
    fun viewQuestion(@PathVariable id: Long, model: Model): String {
        val question = questionService.getQuestionById(id)
        model.addAttribute("question", question)
        
        // Find quizzes that use this question
        val quizzes = quizService.getQuizzesByQuestionId(id)
        model.addAttribute("quizzes", quizzes)
        
        return "admin/question-view"
    }

    @PostMapping("/questions/delete/{id}")
    fun deleteQuestion(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        questionService.deleteQuestion(id)
        redirectAttributes.addFlashAttribute("message", "Question deleted successfully")
        return "redirect:/admin/questions"
    }

    @GetMapping("/questions/api/{id}")
    @ResponseBody
    fun getQuestionJson(@PathVariable id: Long): Question {
        return questionService.getQuestionById(id)
    }

    // Quiz Management
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
            categoryId != null -> quizService.getQuizzesByCategory(categoryId, pageable)
            !search.isNullOrBlank() -> quizService.searchQuizzes(search, pageable)
            else -> quizService.getAllQuizzes(pageable)
        }

        model.addAttribute("quizzes", quizzesPage.content)
        model.addAttribute("currentPage", page)
        model.addAttribute("totalPages", quizzesPage.totalPages)
        model.addAttribute("size", size)
        model.addAttribute("categoryId", categoryId)
        model.addAttribute("search", search)
        model.addAttribute("categories", categoryService.getAllCategories())

        return "admin/quizzes"
    }

    @GetMapping("/quizzes/new")
    fun newQuiz(model: Model): String {
        val quiz = Quiz()
        quiz.isActive = true
        model.addAttribute("quiz", quiz)
        model.addAttribute("categories", categoryService.getAllCategories())
        model.addAttribute("availableQuestions", questionService.getAllQuestions())
        return "admin/quiz-form"
    }

    @PostMapping("/quizzes/save")
    fun saveQuiz(
        @ModelAttribute quiz: Quiz,
        @RequestParam categoryId: Long,
        @RequestParam(required = false) questionIds: List<Long>?,
        redirectAttributes: RedirectAttributes
    ): String {
        val category = categoryService.getCategoryById(categoryId)
        quiz.category = category
        
        // Add questions to quiz
        if (!questionIds.isNullOrEmpty()) {
            val questions = questionService.getQuestionsByIds(questionIds)
            quiz.questions = questions.toMutableSet()
        }
        
        quizService.createQuiz(quiz)
        redirectAttributes.addFlashAttribute("message", "Quiz saved successfully")
        return "redirect:/admin/quizzes"
    }

    @GetMapping("/quizzes/edit/{id}")
    fun editQuiz(@PathVariable id: Long, model: Model): String {
        val quiz = quizService.getQuizById(id)
        model.addAttribute("quiz", quiz)
        model.addAttribute("categories", categoryService.getAllCategories())
        
        // Get all questions that are not already in the quiz
        val allQuestions = questionService.getAllQuestions()
        val quizQuestionIds = quiz.questions?.map { it.id } ?: emptyList()
        val availableQuestions = allQuestions.filter { it.id !in quizQuestionIds }
        
        model.addAttribute("availableQuestions", availableQuestions)
        return "admin/quiz-form"
    }

    @PostMapping("/quizzes/update/{id}")
    fun updateQuiz(
        @PathVariable id: Long,
        @ModelAttribute quiz: Quiz,
        @RequestParam categoryId: Long,
        @RequestParam(required = false) questionIds: List<Long>?,
        redirectAttributes: RedirectAttributes
    ): String {
        val category = categoryService.getCategoryById(categoryId)
        val updatedQuiz = quiz.copy(id = id, category = category)
        
        // Update questions
        if (!questionIds.isNullOrEmpty()) {
            val questions = questionService.getQuestionsByIds(questionIds)
            updatedQuiz.questions = questions.toMutableSet()
        } else {
            updatedQuiz.questions = mutableSetOf()
        }
        
        quizService.updateQuiz(id, updatedQuiz)
        redirectAttributes.addFlashAttribute("message", "Quiz updated successfully")
        return "redirect:/admin/quizzes"
    }

    @GetMapping("/quizzes/view/{id}")
    fun viewQuiz(@PathVariable id: Long, model: Model): String {
        val quiz = quizService.getQuizById(id)
        model.addAttribute("quiz", quiz)
        return "admin/quiz-view"
    }

    @PostMapping("/quizzes/delete/{id}")
    fun deleteQuiz(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        quizService.deleteQuiz(id)
        redirectAttributes.addFlashAttribute("message", "Quiz deleted successfully")
        return "redirect:/admin/quizzes"
    }

    @PostMapping("/quizzes/toggle/{id}")
    fun toggleQuizStatus(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        val quiz = quizService.getQuizById(id)
        val updatedQuiz = quiz.copy(isActive = !quiz.isActive)
        quizService.updateQuiz(id, updatedQuiz)
        
        val status = if (updatedQuiz.isActive) "activated" else "deactivated"
        redirectAttributes.addFlashAttribute("message", "Quiz $status successfully")
        return "redirect:/admin/quizzes"
    }

    // Quiz Attempt Management
    @GetMapping("/attempts")
    fun attempts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) quizId: Long?,
        @RequestParam(required = false) date: String?,
        @RequestParam(required = false) username: String?,
        model: Model
    ): String {
        val pageable = PageRequest.of(page, size, Sort.by("startTime").descending())
        
        val attemptsPage = when {
            quizId != null -> quizAttemptService.getAttemptsByQuiz(quizId, pageable)
            !date.isNullOrBlank() -> {
                val localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
                quizAttemptService.getAttemptsByDate(localDate, pageable)
            }
            !username.isNullOrBlank() -> quizAttemptService.getAttemptsByUsername(username, pageable)
            else -> quizAttemptService.getAllAttempts(pageable)
        }

        model.addAttribute("attempts", attemptsPage.content)
        model.addAttribute("currentPage", page)
        model.addAttribute("totalPages", attemptsPage.totalPages)
        model.addAttribute("size", size)
        model.addAttribute("quizId", quizId)
        model.addAttribute("date", date)
        model.addAttribute("username", username)
        model.addAttribute("quizzes", quizService.getAllQuizzes())

        return "admin/attempts"
    }

    @GetMapping("/attempts/view/{id}")
    fun viewAttempt(@PathVariable id: Long, model: Model): String {
        val attempt = quizAttemptService.getAttemptById(id)
        val responses = attempt.responses ?: emptyList()
        
        // Calculate statistics
        val totalQuestions = attempt.quiz.questions?.size ?: 0
        val correctCount = responses.count { it.isCorrect }
        
        model.addAttribute("attempt", attempt)
        model.addAttribute("responses", responses)
        model.addAttribute("totalQuestions", totalQuestions)
        model.addAttribute("correctCount", correctCount)
        
        return "admin/attempt-view"
    }

    @PostMapping("/attempts/delete/{id}")
    fun deleteAttempt(@PathVariable id: Long, redirectAttributes: RedirectAttributes): String {
        quizAttemptService.deleteAttempt(id)
        redirectAttributes.addFlashAttribute("message", "Quiz attempt deleted successfully")
        return "redirect:/admin/attempts"
    }
} 