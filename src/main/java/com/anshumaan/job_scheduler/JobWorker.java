package com.anshumaan.job_scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;
import java.util.List;

@Component
public class JobWorker {

    @Autowired
    private SqsService sqsService;

    @Autowired
    private JobService jobService;

    // Runs every 5 seconds automatically
    @Scheduled(fixedDelay = 5000)
    public void processJobs() {
        System.out.println("Worker polling SQS queue...");

        List<Message> messages = sqsService.receiveMessages();

        for (Message message : messages) {
            Long jobId = Long.parseLong(message.body());
            System.out.println("Processing job: " + jobId);

            try {
                // Mark job as RUNNING
                jobService.updateJobStatus(jobId, "RUNNING");

                // Simulate job execution (replace with real logic later)
                Thread.sleep(2000);

                // Mark job as DONE
                jobService.updateJobStatus(jobId, "DONE");
                System.out.println("Job " + jobId + " completed successfully!");

            } catch (Exception e) {
                // Mark job as FAILED
                jobService.updateJobStatus(jobId, "FAILED");
                System.out.println("Job " + jobId + " failed: " + e.getMessage());
            } finally {
                // Always delete message from SQS after processing
                sqsService.deleteMessage(message.receiptHandle());
            }
        }
    }
}