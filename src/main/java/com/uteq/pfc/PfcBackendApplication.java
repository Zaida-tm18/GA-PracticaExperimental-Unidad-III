package com.uteq.pfc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * PFC - UTEQ - Facultad de Ciencias de la Computacion
 * Aplicaciones Web [111] - 5to Nivel A
 * Guia de Practica Experimental - Unidad III
 * Stack: Java 21 / Spring Boot 3.x / PostgreSQL 16 / Redis 7.x
 */
@SpringBootApplication
@EnableCaching
public class PfcBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(PfcBackendApplication.class, args);
    }
}
