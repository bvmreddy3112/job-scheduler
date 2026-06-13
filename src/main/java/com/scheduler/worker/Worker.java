package com.scheduler.worker;

 import com.scheduler.core.JobStatus;
 import com.scheduler.core.JobTask;

 import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

 public  class Worker implements Runnable{

    private final PriorityBlockingQueue<JobTask<?>> taskQueue;
    private final ConcurrentHashMap<String,String> resultStore;
    private final int maxRetries;
    private volatile boolean running;

    public Worker(PriorityBlockingQueue<JobTask<?>> taskQueue,
                    ConcurrentHashMap<String,String> resultStore,
                    int maxRetries){

                        this.taskQueue = taskQueue;
                        this.resultStore = resultStore;
                        this.maxRetries = maxRetries;
                        this.running = true;
                    }
    
    @Override
    public void run() {
        while(running){
            try{
                JobTask<?> job = taskQueue.take();
                executeWithRetry(job);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                running = false;
            }
            }
        }

        private void executeWithRetry(JobTask<?> job){
            while(job.getRetryCount() <= maxRetries){
                try{
                    job.execute();
                    resultStore.put(job.getJobId(),"COMPLETED:"+job.getResult());
                    System.out.println("[SUCCESS]"+job.getJobId());
                    return;
                } catch(Exception e){

                    job.incrementRetry();
                    job.setStatus(JobStatus.RETRYING);
                    System.out.println("[RETRY]"+job.getRetryCount()+"]" + job.getJobId());
                }
            }

            job.setStatus(JobStatus.FAILED);
            resultStore.put(job.getJobId(), "FAILED after" +maxRetries+"retries");
            System.out.println("[FAILED]"+job.getJobId());
        }

        public void stop(){
            running = false;
        }
    }

 
