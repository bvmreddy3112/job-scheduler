package com.scheduler.core;

import com.scheduler.worker.Worker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class Scheduler {

    private final PriorityBlockingQueue<JobTask<?>> taskQueue;
    private final ConcurrentHashMap<String, String> resultStore;
    private final ExecutorService threadPool;
    private final int workerCount;
    private final JobRepository jobRepository;

    public Scheduler(int workerCount) {
        this.workerCount = workerCount;
        this.taskQueue = new PriorityBlockingQueue<>();
        this.resultStore = new ConcurrentHashMap<>();
        this.threadPool = Executors.newFixedThreadPool(workerCount);
        this.jobRepository = new JobRepository();
    }

    public void start() {
        for (int i = 0; i < workerCount; i++) {
            Worker worker = new Worker(taskQueue, resultStore, 3, jobRepository);
            threadPool.submit(worker);
        }
        System.out.println("[SCHEDULER] Started with " + workerCount + " workers");
    }

    public void submit(JobTask<?> job, String jobType) {
        jobRepository.saveJob(job, jobType);
        taskQueue.put(job);
        System.out.println("[SUBMITTED] " + job.getJobId() + " priority=" + job.getPriority());
    }

    public String getResult(String jobId) {
        return resultStore.getOrDefault(jobId, "NOT COMPLETED YET");
    }

    public void shutdown() {
        threadPool.shutdown();
        jobRepository.loadPendingJobs();
        System.out.println("[SCHEDULER] Shutting down...");
    }
}