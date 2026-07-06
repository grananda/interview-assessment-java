package com.nttdata.assessment.tasks.dto;

import java.util.Map;

/**
 * Aggregated task statistics. Keys are the API values of the enums
 * (e.g. "in_progress", "high") so the JSON is stable and lowercase.
 */
public record TaskStatsResponse(
        long total,
        Map<String, Long> byStatus,
        Map<String, Long> byPriority) {
}
