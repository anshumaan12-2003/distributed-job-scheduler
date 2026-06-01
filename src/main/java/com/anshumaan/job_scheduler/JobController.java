package com.anshumaan.job_scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    // POST /api/jobs — submit a new job
    @PostMapping
    public ResponseEntity<Job> submitJob(@RequestBody Job job) {
        Job saved = jobService.submitJob(job);
        return ResponseEntity.ok(saved);
    }

    // GET /api/jobs — get all jobs
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // GET /api/jobs/{id} — get job by id
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/jobs/status/{status} — get jobs by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Job>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(jobService.getJobsByStatus(status));
    }

    // PATCH /api/jobs/{id}/status — update job status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Job> updateStatus(@PathVariable Long id,
                                            @RequestParam String status) {
        return ResponseEntity.ok(jobService.updateJobStatus(id, status));
    }
}