package com.yunussemree.ypt.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "quizzes")
data class Quiz(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    var title: String = "",
    
    var description: String? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Category? = null,
    
    @Column(nullable = false)
    var timeLimit: Int = 0, // Time limit in minutes, 0 means no limit
    
    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    var isActive: Boolean = true,
    
    @ManyToMany
    @JoinTable(
        name = "quiz_questions",
        joinColumns = [JoinColumn(name = "quiz_id")],
        inverseJoinColumns = [JoinColumn(name = "question_id")]
    )
    var questions: MutableSet<Question> = mutableSetOf()
) 