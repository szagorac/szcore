package com.xenaksys.szcore.model;

public interface TaskResult {

    boolean isError();

    String getError();

    TaskId getId();

    void reset();

}
