package com.yunussemree.ypt.config

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.yunussemree.ypt.model.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class JacksonConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper {
        val builder = Jackson2ObjectMapperBuilder()
        
        // Configure ObjectMapper
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        builder.modules(JavaTimeModule())
        
        // Avoid infinite recursion with bidirectional relationships
        builder.mixIn(Category::class.java, CategoryMixin::class.java)
        builder.mixIn(Question::class.java, QuestionMixin::class.java)
        builder.mixIn(Answer::class.java, AnswerMixin::class.java)
        builder.mixIn(Quiz::class.java, QuizMixin::class.java)
        builder.mixIn(QuizAttempt::class.java, QuizAttemptMixin::class.java)
        builder.mixIn(QuizResponse::class.java, QuizResponseMixin::class.java)
        
        return builder.build()
    }
}

@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
abstract class CategoryMixin

@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
abstract class QuestionMixin

@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
abstract class AnswerMixin

@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
abstract class QuizMixin

@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
abstract class QuizAttemptMixin

@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
abstract class QuizResponseMixin 