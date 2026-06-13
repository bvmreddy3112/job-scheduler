 
package com.scheduler.main;

import com.scheduler.core.JobTask;
import com.scheduler.core.Scheduler;
import com.scheduler.core.Task;

public class Main {

    public static void main(String[] args) throws InterruptedException{
        Scheduler scheduler = new Scheduler(3);
        scheduler.start();

        scheduler.submit(new JobTask<String>("job-1", 1, new Task<String>() {
            @Override
            public String execute() throws Exception {
                System.out.println("Executing Email Task");
                return "Email sent";
            }
            @Override
            public String getJobId() { return "job-1"; }
            @Override
            public int getPriority() { return 1; }
        }));

        scheduler.submit(new JobTask<String>("job-2", 3, new Task<String>() {
            @Override
            public String execute() throws Exception {
                System.out.println("Executing Invoice Task");
                return "Invoice generated";
            }
            @Override
            public String getJobId() { return "job-2"; }
            @Override
            public int getPriority() { return 3; }
        }));

        scheduler.submit(new JobTask<String>("job-3", 2, new Task<String>() {
            @Override
            public String execute() throws Exception {
                System.out.println("Executing Notification Task");
                return "Notification pushed";
            }
            @Override
            public String getJobId() { return "job-3"; }
            @Override
            public int getPriority() { return 2; }
        }));

        scheduler.submit(new JobTask<String>("job-4", 1, new Task<String>() {
            @Override
            public String execute() throws Exception {
                System.out.println("Executing Payment Task");
                throw new Exception("Payment gateway down");
            }
            @Override
            public String getJobId() { return "job-4"; }
            @Override
            public int getPriority() { return 1; }
        }));


        Thread.sleep(3000);

        System.out.println("\n--- RESULTS ---");
        System.out.println(scheduler.getResult("job-1"));
        System.out.println(scheduler.getResult("job-2"));
        System.out.println(scheduler.getResult("job-3"));
        System.out.println(scheduler.getResult("job-4"));
        
        scheduler.shutdown();

    }
}