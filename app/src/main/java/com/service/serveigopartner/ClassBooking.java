package com.service.serveigopartner;

public class ClassBooking {
    public String services;
    public String comments;
    public String date;
    public String time;
    public String userAddress;
    public String userName;
    public String status;
    public String jobId;
    public String userId;
    public boolean instant;
    public String amount;
    public boolean payment;

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getServices() {
        return services;
    }

    public String getComments() {
        return comments;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserName() {
        return userName;
    }

    public String getStatus() {
        return status;
    }

    public String getJobId(){ return jobId; }

    public String getUserId() {
        return userId;
    }

    public boolean isInstant() {
        return instant;
    }

    public String getAmount() {
        return amount;
    }

    public boolean isPayment() {
        return payment;
    }
}
