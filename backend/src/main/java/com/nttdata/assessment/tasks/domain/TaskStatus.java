package com.nttdata.assessment.tasks.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Lifecycle states a task can be in. API contract uses the snake_case value.
 */
public enum TaskStatus {
    TODO("todo"),
    IN_PROGRESS("in_progress"),
    DONE("done");

    private final String apiValue;

    TaskStatus(String apiValue) {
        this.apiValue = apiValue;
    }

    /**
     * Value exposed over the API (used by Jackson for (de)serialization).
     */
    @JsonValue
    public String apiValue() {
        return apiValue;
    }

    @JsonCreator
    public static TaskStatus fromApiValue(String value) {
        return Arrays.stream(values())
                .filter(status -> status.apiValue.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown task status: " + value));
    }
}
