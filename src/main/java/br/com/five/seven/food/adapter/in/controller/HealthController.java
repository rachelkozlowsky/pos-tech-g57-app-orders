package br.com.five.seven.food.adapter.in.controller;

import br.com.five.seven.food.infra.annotations.health.SwaggerHealth;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "Health check endpoint")
@RestController
@RequestMapping
public class HealthController {

    @SwaggerHealth
    @GetMapping("/health")
    public String health() {
        return "UP!";
    }
}
