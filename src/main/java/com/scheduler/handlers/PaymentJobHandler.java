package com.scheduler.handlers;

import com.scheduler.core.JobHandler;

public class PaymentJobHandler implements JobHandler {
    @Override
    public String execute(String payload) throws Exception {
        System.out.println("Processing payment: " + payload);
        return "Payment processed: " + payload;
    }
}