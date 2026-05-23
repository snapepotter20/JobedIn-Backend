package com.job.controller;

import com.job.dto.PortalDtos.ApplicationResponse;
import com.job.dto.PortalDtos.CandidateProfileRequest;
import com.job.dto.PortalDtos.CandidateResponse;
import com.job.service.AuthService;
import com.job.service.PortalService;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/candidate")
public class CandidateController {

    private final PortalService portalService;
    private final AuthService authService;

    public CandidateController(PortalService portalService, AuthService authService) {
        this.portalService = portalService;
        this.authService = authService;
    }

    @PostMapping("/profile")
    public CandidateResponse saveProfile(@RequestBody CandidateProfileRequest request, Principal principal) {
        return portalService.saveCandidateProfile(authService.getCurrentUser(principal.getName()), request);
    }

    @GetMapping("/profile")
    public CandidateResponse profile(Principal principal) {
        return portalService.getCandidateProfile(authService.getCurrentUser(principal.getName()));
    }

    @GetMapping("/applications")
    public List<ApplicationResponse> applications(Principal principal) {
        return portalService.candidateApplications(authService.getCurrentUser(principal.getName()));
    }
}
