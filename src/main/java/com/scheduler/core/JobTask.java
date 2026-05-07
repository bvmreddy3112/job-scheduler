<<<<<<< HEAD
package com.scheduler.core

public class JobTask<T> implements Task<T>, Comparable<JobTask<T>> {

    private final String jobId;
    private final int priority;
    private JobStatus status;
    private T result;
    private final Task<T> actualTask;
    private int retryCount;

    public JobTask(String jobId , int priority , Task<T> actualTask){

        this.jobId = jobId;
        this.priority = priority;
        this.actualTask = actualTask;
        this.status  =JobStatus.PENDING;
        this.retryCount = 0;

    }

    @Override
    public T execute() throws Exception{

        this.stattus = JobStatus.RUNNING;
        this.result = actualTask.execute();
        this.status = JobStatus.COMPLETED;
        return this.result;
    }

    @Override
    public String getJobId(){ return jobId;}

    @Override
    public int getPriority(){ return priority;}

    public JobStatus getStatus(){ return status;}

    public void setStatus(JobStatus status) {this.status=status;}

    public T getResult(){ return result;}

    public int getRetryCount() { return retryCount;}

    public void incrementRetry(){ retryCount++;}

    @Override
    public int compareTo(JobTask<T> other){

        return Integer.compare(this.priority , other.priority)
    }

}
=======
 
>>>>>>> f334def4172534cf41c274a077d1840234600346
