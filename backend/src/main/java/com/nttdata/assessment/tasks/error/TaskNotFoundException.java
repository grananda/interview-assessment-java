package com.nttdata.assessment.tasks.error;

/** Domain error raised when a task does not exist. */
public class TaskNotFoundException extends RuntimeException {

    private final long taskId;

    public TaskNotFoundException(long taskId) {
        super("Task " + taskId + " not found");
        this.taskId = taskId;
    }

    public long getTaskId() {
        return taskId;
    }
}
