package com.yunussemree.ypt.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class HomeController {

    @GetMapping
    fun home(): String {
        // Redirect to admin dashboard
        return "redirect:/admin"
    }
} 