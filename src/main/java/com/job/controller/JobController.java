package com.job.controller;

import com.job.dto.PortalDtos.ApplicationResponse;
import com.job.dto.PortalDtos.ApplyRequest;
import com.job.dto.PortalDtos.JobResponse;
import com.job.service.AuthService;
import com.job.service.PortalService;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final PortalService portalService;
    private final AuthService authService;

    public JobController(PortalService portalService, AuthService authService) {
        this.portalService = portalService;
        this.authService = authService;
    }

    @GetMapping
    public List<JobResponse> getApprovedJobs() {
        return portalService.approvedJobs();
    }

    @GetMapping("/{id}")
    public JobResponse jobDetails(@PathVariable Long id) {
        return portalService.jobDetails(id);
    }

    @PostMapping("/{id}/apply")
    public ApplicationResponse apply(@PathVariable Long id, @RequestBody ApplyRequest request, Principal principal) {
        return portalService.apply(authService.getCurrentUser(principal.getName()), id, request);
    }
}
