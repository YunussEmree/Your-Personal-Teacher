package com.yunussemree.ypt.service

import com.yunussemree.ypt.model.Category
import com.yunussemree.ypt.repository.CategoryRepository
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {
    
    fun getAllCategories(): List<Category> = categoryRepository.findAll()
    
    fun getCategoryById(id: Long): Category? = categoryRepository.findById(id).orElse(null)
    
    fun createCategory(category: Category): Category = categoryRepository.save(category)
    
    fun updateCategory(id: Long, categoryDetails: Category): Category {
        val category = getCategoryById(id) ?: throw RuntimeException("Category not found")
        category.name = categoryDetails.name
        category.description = categoryDetails.description
        return categoryRepository.save(category)
    }
    
    fun deleteCategory(id: Long) {
        val category = getCategoryById(id) ?: throw RuntimeException("Category not found")
        categoryRepository.delete(category)
    }
    
    @PostConstruct
    fun initDefaultCategories() {
        if (categoryRepository.count() == 0L) {
            val defaultCategories = listOf(
                Category(name = "Software", description = "Questions about programming and software development"),
                Category(name = "Animals", description = "Questions about different species of animals"),
                Category(name = "Plants", description = "Questions about plant biology and species"),
                Category(name = "Countries", description = "Questions about geography and nations")
            )
            categoryRepository.saveAll(defaultCategories)
        }
    }
} 