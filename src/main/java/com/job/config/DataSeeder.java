package com.job.config;

import com.job.model.ApprovalStatus;
import com.job.model.CandidateProfile;
import com.job.model.CompanyProfile;
import com.job.model.Job;
import com.job.model.Role;
import com.job.model.User;
import com.job.repository.CandidateProfileRepository;
import com.job.repository.CompanyProfileRepository;
import com.job.repository.JobRepository;
import com.job.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CompanyProfileRepository companyRepository;
    private final CandidateProfileRepository candidateRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, CompanyProfileRepository companyRepository,
                      CandidateProfileRepository candidateRepository, JobRepository jobRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User admin = getOrCreateUser("Admin User", "admin@jobapp.com", "password", Role.ADMIN);
        User recruiter = getOrCreateUser("Recruiter User", "recruiter@jobapp.com", "password", Role.RECRUITER);
        User candidate = getOrCreateUser("Candidate User", "candidate@jobapp.com", "password", Role.CANDIDATE);

        CompanyProfile company = companyRepository.findByRecruiterId(recruiter.getId()).orElseGet(CompanyProfile::new);
        if (company.getId() == null) {
            company.setRecruiter(recruiter);
            company.setCompanyName("Acme Careers");
            company.setWebsite("https://acme.example");
            company.setLocation("Remote");
            company.setDescription("A sample verified company for demo and testing.");
            company.setStatus(ApprovalStatus.APPROVED);
            companyRepository.save(company);
        }

        if (candidateRepository.findByCandidateId(candidate.getId()).isEmpty()) {
            CandidateProfile profile = new CandidateProfile();
            profile.setCandidate(candidate);
            profile.setPhone("9999999999");
            profile.setLocation("India");
            profile.setResumeUrl("https://example.com/resume.pdf");
            profile.setSkills("Java, Spring Boot, React, PostgreSQL");
            profile.setSummary("Backend-focused candidate profile for demo applications.");
            candidateRepository.save(profile);
        }

        if (!jobRepository.existsByTitle("Junior Spring Boot Developer")) {
            Job approvedJob = new Job();
            approvedJob.setCompany(company);
            approvedJob.setTitle("Junior Spring Boot Developer");
            approvedJob.setDescription("Build REST APIs, secure endpoints, and work with PostgreSQL.");
            approvedJob.setLocation("Remote");
            approvedJob.setSalaryRange("6-10 LPA");
            approvedJob.setEmploymentType("Full-time");
            approvedJob.setStatus(ApprovalStatus.APPROVED);
            jobRepository.save(approvedJob);
        }

        if (!jobRepository.existsByTitle("React Frontend Developer")) {
            Job pendingJob = new Job();
            pendingJob.setCompany(company);
            pendingJob.setTitle("React Frontend Developer");
            pendingJob.setDescription("Create clean dashboards and candidate-facing job portal screens.");
            pendingJob.setLocation("Bengaluru");
            pendingJob.setSalaryRange("5-9 LPA");
            pendingJob.setEmploymentType("Full-time");
            pendingJob.setStatus(ApprovalStatus.PENDING);
            jobRepository.save(pendingJob);
        }
    }

    private User getOrCreateUser(String name, String email, String password, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> userRepository.save(createUser(name, email, password, role)));
    }

    private User createUser(String name, String email, String password, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setVerified(true);
        return user;
    }
}
