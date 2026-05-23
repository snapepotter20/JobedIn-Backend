package com.job.dto;

public record VerifyOtpRequest(
        String email,
        String otp
) {}
