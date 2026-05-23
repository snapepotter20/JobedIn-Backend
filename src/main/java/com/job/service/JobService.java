package com.job.service;

import com.job.model.Job;
import com.job.model.ApprovalStatus;
import com.job.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> getApprovedJobs() {
        return jobRepository.findByStatus(ApprovalStatus.APPROVED);
    }
}
