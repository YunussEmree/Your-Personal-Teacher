package com.yunussemree.ypt.controller

import com.yunussemree.ypt.model.Category
import com.yunussemree.ypt.service.CategoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping
    fun getAllCategories(): ResponseEntity<List<Category>> {
        val categories = categoryService.getAllCategories()
        return ResponseEntity.ok(categories)
    }

    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: Long): ResponseEntity<Category> {
        val category = categoryService.getCategoryById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(category)
    }

    @PostMapping
    fun createCategory(@RequestBody category: Category): ResponseEntity<Category> {
        val createdCategory = categoryService.createCategory(category)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory)
    }

    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable id: Long,
        @RequestBody categoryDetails: Category
    ): ResponseEntity<Category> {
        return try {
            val updatedCategory = categoryService.updateCategory(id, categoryDetails)
            ResponseEntity.ok(updatedCategory)
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            categoryService.deleteCategory(id)
            ResponseEntity.noContent().build()
        } catch (e: RuntimeException) {
            ResponseEntity.notFound().build()
        }
    }
} 