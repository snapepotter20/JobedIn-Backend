package com.job.repository;

import com.job.model.Job;
import com.job.model.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(ApprovalStatus status);
    List<Job> findByCompanyId(Long companyId);
    long countByStatus(ApprovalStatus status);
    boolean existsByTitle(String title);

}
