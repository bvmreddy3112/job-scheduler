//DAY-1
package com.scheduler.core;

public interface Task<T>{

    T execute() throws Exception;
    String getJobId();
    int getPriority();
}
