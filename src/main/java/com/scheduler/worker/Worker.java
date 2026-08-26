package com.scheduler.worker;

import com.scheduler.core.JobStatus;
import com.scheduler.core.JobTask;
import com.scheduler.core.JobRepository;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Worker implements Runnable {

    private final PriorityBlockingQueue<JobTask<?>> taskQueue;
    private final ConcurrentHashMap<String, String> resultStore;
    private final int maxRetries;
    private volatile boolean running;
    private final JobRepository jobRepository;

    public Worker(PriorityBlockingQueue<JobTask<?>> taskQueue,
                  ConcurrentHashMap<String, String> resultStore,
                  int maxRetries,
                  JobRepository jobRepository) {
        this.taskQueue = taskQueue;
        this.resultStore = resultStore;
        this.maxRetries = maxRetries;
        this.jobRepository = jobRepository;
        this.running = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                JobTask<?> job = taskQueue.take();
                executeWithRetry(job);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    private void executeWithRetry(JobTask<?> job) {
        while (job.getRetryCount() < maxRetries) {
            long startTime = System.currentTimeMillis();
            try {
                job.execute();
                long executionTime = System.currentTimeMillis() - startTime;

                resultStore.put(job.getJobId(), "COMPLETED: " + job.getResult());
                jobRepository.updateJobStatus(job.getJobId(), JobStatus.COMPLETED,
                        String.valueOf(job.getResult()), null);
                jobRepository.logExecution(job.getJobId(), job.getRetryCount() + 1,
                        JobStatus.COMPLETED, executionTime, String.valueOf(job.getResult()), null);

                System.out.println("[SUCCESS] " + job.getJobId());
                return;

            } catch (Exception e) {
                long executionTime = System.currentTimeMillis() - startTime;
                job.incrementRetry();
                job.setStatus(JobStatus.RETRYING);

                jobRepository.updateJobStatus(job.getJobId(), JobStatus.RETRYING, null, e.getMessage());
                jobRepository.logExecution(job.getJobId(), job.getRetryCount(),
                        JobStatus.RETRYING, executionTime, null, e.getMessage());

                System.out.println("[RETRY " + job.getRetryCount() + "] " + job.getJobId());
            }
        }

        job.setStatus(JobStatus.FAILED);
        resultStore.put(job.getJobId(), "FAILED after " + maxRetries + " retries");
        jobRepository.updateJobStatus(job.getJobId(), JobStatus.FAILED, null, "Max retries exhausted");
        jobRepository.moveToDeadLetter(job, "UNKNOWN", "Max retries exhausted");

        System.out.println("[FAILED] " + job.getJobId());
    }

    public void stop() {
        running = false;
    }
}