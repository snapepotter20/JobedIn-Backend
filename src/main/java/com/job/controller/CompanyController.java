package com.job.controller;

import com.job.dto.PortalDtos.ApplicationResponse;
import com.job.dto.PortalDtos.ApplicationStatusRequest;
import com.job.dto.PortalDtos.CompanyRequest;
import com.job.dto.PortalDtos.CompanyResponse;
import com.job.dto.PortalDtos.JobRequest;
import com.job.dto.PortalDtos.JobResponse;
import com.job.service.AuthService;
import com.job.service.PortalService;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
public class CompanyController {

    private final PortalService portalService;
    private final AuthService authService;

    public CompanyController(PortalService portalService, AuthService authService) {
        this.portalService = portalService;
        this.authService = authService;
    }

    @PostMapping("/profile")
    public CompanyResponse saveProfile(@RequestBody CompanyRequest request, Principal principal) {
        return portalService.saveCompanyProfile(authService.getCurrentUser(principal.getName()), request);
    }

    @GetMapping("/profile")
    public CompanyResponse profile(Principal principal) {
        return portalService.getCompanyProfile(authService.getCurrentUser(principal.getName()));
    }

    @PostMapping("/jobs")
    public JobResponse createJob(@RequestBody JobRequest request, Principal principal) {
        return portalService.createJob(authService.getCurrentUser(principal.getName()), request);
    }

    @GetMapping("/jobs")
    public List<JobResponse> jobs(Principal principal) {
        return portalService.recruiterJobs(authService.getCurrentUser(principal.getName()));
    }

    @GetMapping("/jobs/{id}/applications")
    public List<ApplicationResponse> applications(@PathVariable Long id, Principal principal) {
        return portalService.applicationsForJob(authService.getCurrentUser(principal.getName()), id);
    }

    @PutMapping("/applications/{id}/status")
    public ApplicationResponse updateApplication(@PathVariable Long id, @RequestBody ApplicationStatusRequest request,
                                                 Principal principal) {
        return portalService.updateApplicationStatus(authService.getCurrentUser(principal.getName()), id, request);
    }
}
