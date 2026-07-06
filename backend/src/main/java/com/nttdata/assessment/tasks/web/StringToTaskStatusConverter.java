package com.nttdata.assessment.tasks.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.nttdata.assessment.tasks.domain.TaskStatus;

/**
 * Converts the {@code ?status=} query param (e.g. "in_progress") into a
 * {@link TaskStatus}. Spring binds query params via the ConversionService,
 * not Jackson, so the enum's {@code @JsonCreator} is not used here.
 */
@Component
public class StringToTaskStatusConverter implements Converter<String, TaskStatus> {

    @Override
    public TaskStatus convert(String source) {
        return TaskStatus.fromApiValue(source);
    }
}
