package com.job.controller;

import com.job.dto.AuthDtos.AuthResponse;
import com.job.dto.AuthDtos.LoginRequest;
import com.job.dto.AuthDtos.RegisterRequest;
import com.job.dto.AuthDtos.UserResponse;
import com.job.dto.VerifyOtpRequest;
import com.job.service.AuthService;
import java.security.Principal;

import com.job.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("http://localhost:5174")
public class AuthController {

    @Autowired
    private EmailService emailService;
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(Principal principal) {
        return authService.toUserResponse(authService.getCurrentUser(principal.getName()));
    }

    @PostMapping("/verify")
    public AuthResponse verifyOtp(
            @RequestBody VerifyOtpRequest request
    ) {
        return authService.verifyOtp(request);
    }

    @GetMapping("/test-email")
    public String testEmail() {
        emailService.sendOtp("hillloo2030@gmail", "123456");
        return "sent";
    }
}
