package com.nttdata.assessment.tasks.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Relative importance of a task. API contract uses the lowercase value.
 */
public enum TaskPriority {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private final String apiValue;

    TaskPriority(String apiValue) {
        this.apiValue = apiValue;
    }

    @JsonValue
    public String apiValue() {
        return apiValue;
    }

    @JsonCreator
    public static TaskPriority fromApiValue(String value) {
        return Arrays.stream(values())
                .filter(priority -> priority.apiValue.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown task priority: " + value));
    }
}
