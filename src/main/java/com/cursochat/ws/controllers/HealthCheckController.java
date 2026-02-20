package com.cursochat.ws.controllers;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;

public class HealthCheckController {

    private final static  Logger LOGGER = Logger.getLogger(HealthCheckController.class.getName());

    @GetMapping
    void healthCheck() {LOGGER.info("Health check");}
}
