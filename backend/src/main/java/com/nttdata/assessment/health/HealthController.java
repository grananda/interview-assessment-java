package com.nttdata.assessment.health;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Minimal health endpoint used to verify the frontend ↔ backend wiring.
 * Exposed at {@code GET /api/health} (the {@code /api} prefix comes from the
 * servlet context-path configured in application.properties).
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "service", "angular-java-assessment-backend",
                "timestamp", OffsetDateTime.now().toString());
    }
}
