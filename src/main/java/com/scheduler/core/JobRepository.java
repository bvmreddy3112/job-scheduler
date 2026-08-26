package com.scheduler.core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobRepository {
    public void saveJob(JobTask<?> job, String jobType){
        String sql = "INSERT INTO jobs (job_id , job_type , priority , status , max_retries)" + "VALUES (?,?,?,?,?)";
        try(Connection conn = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1,job.getJobId());
                stmt.setString(2,jobType);
                stmt.setInt(3,job.getPriority());
                stmt.setString(4,job.getStatus().name());
                stmt.setInt(5,3);
                stmt.executeUpdate();
            } catch (SQLException e){
                System.err.println("[DB ERROR] Failed to save job:" + e.getMessage());
            }
    }

    public void updateJobStatus(String jobId , JobStatus status , String result , String errorMessage) {
        String sql = "UPDATE jobs SET status = ?, result = ? , error_message = ? ," + "updated_at = CURRENT_TIMESTAMP WHERE job_id = ?";
        try(Connection conn = DatabaseConfig.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, status.name());
                stmt.setString(2,result);
                stmt.setString(3,errorMessage);
                stmt.setString(4,jobId);
                stmt.executeUpdate();

            }

            catch (SQLException e){
                 System.err.println("[DB ERROR] Failed to save job status:" + e.getMessage());
            }
    }

    public void logExecution(String jobId , int attemptNumber , JobStatus status, long executionTimeMs , String result , String errorMessage){
        String sql = "INSERT INTO job_execution_logs " +
                     "(job_id, attempt_number, status, execution_time_ms, result, error_message) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jobId);
            stmt.setInt(2, attemptNumber);
            stmt.setString(3, status.name());
            stmt.setLong(4, executionTimeMs);
            stmt.setString(5, result);
            stmt.setString(6, errorMessage);
            stmt.executeUpdate();
    }
    catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to log execution: " + e.getMessage());
        }
    }
    public void moveToDeadLetter(JobTask<?> job , String jobType, String failureReason){
             String sql = "INSERT INTO dead_letter_jobs " +
                     "(job_id, job_type, priority, failure_reason, retry_count) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, job.getJobId());
            stmt.setString(2, jobType);
            stmt.setInt(3, job.getPriority());
            stmt.setString(4, failureReason);
            stmt.setInt(5, job.getRetryCount());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to move to dead letter: " + e.getMessage());
        }
    }

    public List<JobTask<?>> loadPendingJobs() {
        String sql = "SELECT job_id, priority, status, retry_count FROM jobs " +
                     "WHERE status IN ('PENDING', 'RETRYING') ORDER BY priority ASC";
        List<JobTask<?>> pendingJobs = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("[RECOVERY] Found unfinished job: " + rs.getString("job_id"));
            }

        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to load pending jobs: " + e.getMessage());
        }
        return pendingJobs;
    }
    } 

