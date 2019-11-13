package com.xenaksys.szcore.task;

import com.xenaksys.szcore.model.TaskId;
import com.xenaksys.szcore.model.TaskResult;

public class TaskResultImpl implements TaskResult {

    private String error;
    private boolean isError;
    private TaskId taskId;

    public TaskResultImpl() {
    }

    @Override
    public boolean isError() {
        return isError;
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public TaskId getId() {
        return taskId;
    }

    @Override
    public void reset() {
        error = null;
        isError = false;
        taskId = null;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setIsError(boolean error) {
        isError = error;
    }

    public void setTaskId(TaskId taskId) {
        this.taskId = taskId;
    }
}
