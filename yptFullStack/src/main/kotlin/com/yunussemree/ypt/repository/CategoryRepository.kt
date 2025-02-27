package com.yunussemree.ypt.repository

import com.yunussemree.ypt.model.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByName(name: String): Category?
} 