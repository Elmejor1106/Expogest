package com.expogest.expogest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración personalizada de Spring MVC
 * Registra convertidores personalizados para formularios HTML
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RolConverter rolConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(rolConverter);
    }
}
