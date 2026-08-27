package com.scheduler.core;

public interface JobHandler {
    String execute(String payload) throws Exception;
}
