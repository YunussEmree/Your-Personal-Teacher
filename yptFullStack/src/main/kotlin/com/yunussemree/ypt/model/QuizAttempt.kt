package com.yunussemree.ypt.model

import jakarta.persistence.*
import java.time.LocalDateTime
import com.yunussemree.ypt.model.QuizResponse

@Entity
@Table(name = "quiz_attempts")
data class QuizAttempt(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    var quiz: Quiz = Quiz(),
    
    @Column(nullable = false)
    var userName: String = "", // Will be replaced with User entity later
    
    @Column(nullable = false)
    var startTime: LocalDateTime = LocalDateTime.now(),
    
    var endTime: LocalDateTime? = null,
    
    var score: Int = 0,
    
    @Column(nullable = false)
    var isCompleted: Boolean = false,
    
    @OneToMany(mappedBy = "quizAttempt", cascade = [CascadeType.ALL], orphanRemoval = true)
    var responses: MutableList<QuizResponse> = mutableListOf()
) 