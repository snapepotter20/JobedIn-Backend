package com.job.repository;

import com.job.model.ApprovalStatus;
import com.job.model.CompanyProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
    Optional<CompanyProfile> findByRecruiterId(Long recruiterId);
    List<CompanyProfile> findByStatus(ApprovalStatus status);
    long countByStatus(ApprovalStatus status);
}
