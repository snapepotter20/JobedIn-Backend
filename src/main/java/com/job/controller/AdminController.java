package com.job.controller;

import com.job.dto.PortalDtos.ApplicationResponse;
import com.job.dto.PortalDtos.CandidateResponse;
import com.job.dto.PortalDtos.CompanyResponse;
import com.job.dto.PortalDtos.DashboardResponse;
import com.job.dto.PortalDtos.JobResponse;
import com.job.dto.PortalDtos.UserSummary;
import com.job.model.ApprovalStatus;
import com.job.service.PortalService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PortalService portalService;

    public AdminController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return portalService.dashboard();
    }

    @GetMapping("/companies/pending")
    public List<CompanyResponse> pendingCompanies() {
        return portalService.pendingCompanies();
    }

    @PutMapping("/companies/{id}/verify")
    public CompanyResponse verifyCompany(@PathVariable Long id) {
        return portalService.updateCompanyStatus(id, ApprovalStatus.APPROVED);
    }

    @PutMapping("/companies/{id}/reject")
    public CompanyResponse rejectCompany(@PathVariable Long id) {
        return portalService.updateCompanyStatus(id, ApprovalStatus.REJECTED);
    }

    @GetMapping("/jobs/pending")
    public List<JobResponse> pendingJobs() {
        return portalService.pendingJobs();
    }

    @PutMapping("/jobs/{id}/approve")
    public JobResponse approveJob(@PathVariable Long id) {
        return portalService.updateJobStatus(id, ApprovalStatus.APPROVED);
    }

    @PutMapping("/jobs/{id}/reject")
    public JobResponse rejectJob(@PathVariable Long id) {
        return portalService.updateJobStatus(id, ApprovalStatus.REJECTED);
    }

    @GetMapping("/candidates")
    public List<CandidateResponse> candidates() {
        return portalService.candidates();
    }

    @GetMapping("/applications")
    public List<ApplicationResponse> applications() {
        return portalService.allApplications();
    }

    @GetMapping("/users")
    public List<UserSummary> users() {
        return portalService.userSummaries();
    }
}
