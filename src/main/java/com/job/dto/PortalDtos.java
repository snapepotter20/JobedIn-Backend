package com.job.dto;

import com.job.model.ApplicationStatus;
import com.job.model.ApprovalStatus;
import com.job.model.Role;
import java.time.LocalDateTime;

public class PortalDtos {
    public record CompanyRequest(String companyName, String website, String location, String description) {}
    public record CandidateProfileRequest(String phone, String location, String resumeUrl, String skills, String summary) {}
    public record JobRequest(String title, String description, String location, String salaryRange, String employmentType) {}
    public record ApplyRequest(String coverLetter) {}
    public record ApplicationStatusRequest(ApplicationStatus status) {}

    public record CompanyResponse(Long id, Long recruiterId, String recruiterName, String companyName, String website,
                                  String location, String description, ApprovalStatus status) {}

    public record CandidateResponse(Long id, Long candidateId, String candidateName, String email, String phone,
                                    String location, String resumeUrl, String skills, String summary) {}

    public record JobResponse(Long id, String title, String description, String location, String salaryRange,
                              String employmentType, ApprovalStatus status, Long companyId, String companyName) {}

    public record ApplicationResponse(Long id, Long jobId, String jobTitle, String companyName, Long candidateId,
                                      String candidateName, String candidateEmail, String coverLetter,
                                      ApplicationStatus status, LocalDateTime appliedAt) {}

    public record DashboardResponse(long totalJobs, long companies, long candidates, long pendingCompanies,
                                    long pendingJobs, long applications) {}

    public record UserSummary(Long id, String name, String email, Role role) {}
}
