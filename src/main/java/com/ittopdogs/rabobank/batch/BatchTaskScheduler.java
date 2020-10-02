package com.ittopdogs.rabobank.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BatchTaskScheduler {
    private final Job job;
    private final JobLauncher jobLauncher;

    @Scheduled(cron = "*/10 * * * * *")
    public void run() {
        try {
            JobExecution execution = jobLauncher.run(job, new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters());
            log.info("Job Status: {}", execution.getStatus());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            log.info("Job done");
        }
    }
}
