package com.yunussemree.ypt.model

import jakarta.persistence.*

@Entity
@Table(name = "quiz_responses")
data class QuizResponse(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    var quizAttempt: QuizAttempt = QuizAttempt(),
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    var question: Question = Question(),
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_answer_id", nullable = false)
    var selectedAnswer: Answer = Answer(),
    
    @Column(nullable = false)
    var isCorrect: Boolean = false
) 