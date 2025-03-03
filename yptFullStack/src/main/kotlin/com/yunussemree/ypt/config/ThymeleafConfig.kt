package com.yunussemree.ypt.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver

@Configuration
class ThymeleafConfig {
    
    /**
     * Thymeleaf 3.1+ removes web request objects (#request, #session, etc.) by default.
     * This configuration re-enables these objects for backward compatibility.
     */
    @Bean
    @Primary
    fun templateEngine(templateResolver: ITemplateResolver): SpringTemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.setTemplateResolver(templateResolver)
        
        // Enable the SpringEL compiler for better performance
        templateEngine.enableSpringELCompiler = true
        
        // This is the key setting that re-enables web request objects
        templateEngine.addDialect(org.thymeleaf.spring6.dialect.SpringStandardDialect())
        
        return templateEngine
    }
} 