package com.job.service;

import com.job.dto.PortalDtos.ApplicationResponse;
import com.job.dto.PortalDtos.ApplicationStatusRequest;
import com.job.dto.PortalDtos.ApplyRequest;
import com.job.dto.PortalDtos.CandidateProfileRequest;
import com.job.dto.PortalDtos.CandidateResponse;
import com.job.dto.PortalDtos.CompanyRequest;
import com.job.dto.PortalDtos.CompanyResponse;
import com.job.dto.PortalDtos.DashboardResponse;
import com.job.dto.PortalDtos.JobRequest;
import com.job.dto.PortalDtos.JobResponse;
import com.job.dto.PortalDtos.UserSummary;
import com.job.model.ApplicationStatus;
import com.job.model.ApprovalStatus;
import com.job.model.CandidateProfile;
import com.job.model.CompanyProfile;
import com.job.model.Job;
import com.job.model.JobApplication;
import com.job.model.Role;
import com.job.model.User;
import com.job.repository.CandidateProfileRepository;
import com.job.repository.CompanyProfileRepository;
import com.job.repository.JobApplicationRepository;
import com.job.repository.JobRepository;
import com.job.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PortalService {

    private final UserRepository userRepository;
    private final CompanyProfileRepository companyRepository;
    private final CandidateProfileRepository candidateRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;

    public PortalService(UserRepository userRepository, CompanyProfileRepository companyRepository,
                         CandidateProfileRepository candidateRepository, JobRepository jobRepository,
                         JobApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    public DashboardResponse dashboard() {
        return new DashboardResponse(
                jobRepository.count(),
                userRepository.countByRole(Role.RECRUITER),
                userRepository.countByRole(Role.CANDIDATE),
                companyRepository.countByStatus(ApprovalStatus.PENDING),
                jobRepository.countByStatus(ApprovalStatus.PENDING),
                applicationRepository.count()
        );
    }

    public List<CompanyResponse> pendingCompanies() {
        return companyRepository.findByStatus(ApprovalStatus.PENDING).stream().map(this::toCompanyResponse).toList();
    }

    public CompanyResponse updateCompanyStatus(Long companyId, ApprovalStatus status) {
        CompanyProfile company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        company.setStatus(status);
        return toCompanyResponse(companyRepository.save(company));
    }

    public List<JobResponse> pendingJobs() {
        return jobRepository.findByStatus(ApprovalStatus.PENDING).stream().map(this::toJobResponse).toList();
    }

    public JobResponse updateJobStatus(Long jobId, ApprovalStatus status) {
        Job job = getJob(jobId);
        job.setStatus(status);
        return toJobResponse(jobRepository.save(job));
    }

    public List<CandidateResponse> candidates() {
        return candidateRepository.findAll().stream().map(this::toCandidateResponse).toList();
    }

    public List<ApplicationResponse> allApplications() {
        return applicationRepository.findAll().stream().map(this::toApplicationResponse).toList();
    }

    public CompanyResponse saveCompanyProfile(User recruiter, CompanyRequest request) {
        CompanyProfile profile = companyRepository.findByRecruiterId(recruiter.getId()).orElseGet(CompanyProfile::new);
        profile.setRecruiter(recruiter);
        profile.setCompanyName(request.companyName());
        profile.setWebsite(request.website());
        profile.setLocation(request.location());
        profile.setDescription(request.description());
        profile.setStatus(ApprovalStatus.PENDING);
        return toCompanyResponse(companyRepository.save(profile));
    }

    public CompanyResponse getCompanyProfile(User recruiter) {
        return toCompanyResponse(getCompanyForRecruiter(recruiter));
    }

    public JobResponse createJob(User recruiter, JobRequest request) {
        CompanyProfile company = getCompanyForRecruiter(recruiter);
        if (company.getStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalStateException("Company must be verified before posting jobs");
        }

        Job job = new Job();
        job.setCompany(company);
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setLocation(request.location());
        job.setSalaryRange(request.salaryRange());
        job.setEmploymentType(request.employmentType());
        job.setStatus(ApprovalStatus.PENDING);
        return toJobResponse(jobRepository.save(job));
    }

    public List<JobResponse> recruiterJobs(User recruiter) {
        CompanyProfile company = getCompanyForRecruiter(recruiter);
        return jobRepository.findByCompanyId(company.getId()).stream().map(this::toJobResponse).toList();
    }

    public List<ApplicationResponse> applicationsForJob(User recruiter, Long jobId) {
        Job job = getJob(jobId);
        CompanyProfile company = getCompanyForRecruiter(recruiter);
        if (!job.getCompany().getId().equals(company.getId())) {
            throw new IllegalArgumentException("This job belongs to another company");
        }
        return applicationRepository.findByJobId(jobId).stream().map(this::toApplicationResponse).toList();
    }

    public ApplicationResponse updateApplicationStatus(User recruiter, Long applicationId, ApplicationStatusRequest request) {
        JobApplication application = getApplication(applicationId);
        CompanyProfile company = getCompanyForRecruiter(recruiter);
        if (!application.getJob().getCompany().getId().equals(company.getId())) {
            throw new IllegalArgumentException("This application belongs to another company");
        }
        ApplicationStatus status = request.status() == null ? ApplicationStatus.APPLIED : request.status();
        application.setStatus(status);
        return toApplicationResponse(applicationRepository.save(application));
    }

    public CandidateResponse saveCandidateProfile(User candidate, CandidateProfileRequest request) {
        CandidateProfile profile = candidateRepository.findByCandidateId(candidate.getId()).orElseGet(CandidateProfile::new);
        profile.setCandidate(candidate);
        profile.setPhone(request.phone());
        profile.setLocation(request.location());
        profile.setResumeUrl(request.resumeUrl());
        profile.setSkills(request.skills());
        profile.setSummary(request.summary());
        return toCandidateResponse(candidateRepository.save(profile));
    }

    public List<JobResponse> approvedJobs() {
        return jobRepository.findByStatus(ApprovalStatus.APPROVED).stream().map(this::toJobResponse).toList();
    }

    public JobResponse jobDetails(Long jobId) {
        Job job = getJob(jobId);
        if (job.getStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalArgumentException("Job is not approved yet");
        }
        return toJobResponse(job);
    }

    public ApplicationResponse apply(User candidate, Long jobId, ApplyRequest request) {
        Job job = getJob(jobId);
        if (job.getStatus() != ApprovalStatus.APPROVED) {
            throw new IllegalArgumentException("Job is not open for applications");
        }
        if (applicationRepository.existsByJobIdAndCandidateId(jobId, candidate.getId())) {
            throw new IllegalStateException("You have already applied to this job");
        }
        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setCoverLetter(request.coverLetter());
        return toApplicationResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> candidateApplications(User candidate) {
        return applicationRepository.findByCandidateId(candidate.getId()).stream().map(this::toApplicationResponse).toList();
    }

    public CandidateResponse getCandidateProfile(User candidate) {
        return candidateRepository.findByCandidateId(candidate.getId()).map(this::toCandidateResponse).orElse(null);
    }

    public List<UserSummary> userSummaries() {
        return userRepository.findAll().stream()
                .map(user -> new UserSummary(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .toList();
    }

    public JobResponse toJobResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getSalaryRange(),
                job.getEmploymentType(),
                job.getStatus(),
                job.getCompany().getId(),
                job.getCompany().getCompanyName()
        );
    }

    private CompanyProfile getCompanyForRecruiter(User recruiter) {
        return companyRepository.findByRecruiterId(recruiter.getId())
                .orElseThrow(() -> new IllegalStateException("Create a company profile first"));
    }

    private Job getJob(Long jobId) {
        return jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job not found"));
    }

    private JobApplication getApplication(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    }

    private CompanyResponse toCompanyResponse(CompanyProfile company) {
        User recruiter = company.getRecruiter();
        return new CompanyResponse(company.getId(), recruiter.getId(), recruiter.getName(), company.getCompanyName(),
                company.getWebsite(), company.getLocation(), company.getDescription(), company.getStatus());
    }

    private CandidateResponse toCandidateResponse(CandidateProfile profile) {
        User candidate = profile.getCandidate();
        return new CandidateResponse(profile.getId(), candidate.getId(), candidate.getName(), candidate.getEmail(),
                profile.getPhone(), profile.getLocation(), profile.getResumeUrl(), profile.getSkills(), profile.getSummary());
    }

    private ApplicationResponse toApplicationResponse(JobApplication application) {
        Job job = application.getJob();
        User candidate = application.getCandidate();
        return new ApplicationResponse(application.getId(), job.getId(), job.getTitle(), job.getCompany().getCompanyName(),
                candidate.getId(), candidate.getName(), candidate.getEmail(), application.getCoverLetter(),
                application.getStatus(), application.getAppliedAt());
    }
}
