package com.job.service;

import com.job.dto.AuthDtos.AuthResponse;
import com.job.dto.AuthDtos.LoginRequest;
import com.job.dto.AuthDtos.RegisterRequest;
import com.job.dto.AuthDtos.UserResponse;
import com.job.dto.VerifyOtpRequest;
import com.job.model.CandidateProfile;
import com.job.model.CompanyProfile;
import com.job.model.Role;
import com.job.model.User;
import com.job.repository.CandidateProfileRepository;
import com.job.repository.CompanyProfileRepository;
import com.job.repository.UserRepository;
import com.job.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CandidateProfileRepository candidateRepository;
    private final CompanyProfileRepository recruiterRepository;
    private final EmailService emailService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            CandidateProfileRepository candidateRepository,
            CompanyProfileRepository recruiterRepository,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.candidateRepository = candidateRepository;
        this.recruiterRepository = recruiterRepository;
        this.emailService = emailService;
    }

//    public AuthResponse register(RegisterRequest request) {
//
//        if (userRepository.existsByEmail(request.email())) {
//            throw new IllegalArgumentException("Email is already registered");
//        }
//
//        Role role = request.role() == null
//                ? Role.CANDIDATE
//                : request.role();
//
//        User user = new User();
//
//        user.setName(request.name());
//        user.setEmail(request.email());
//        user.setPassword(passwordEncoder.encode(request.password()));
//        user.setRole(role);
//
//        User saved = userRepository.save(user);
//
//        // Create candidate profile
//        if (role == Role.CANDIDATE) {
//
//            CandidateProfile candidateProfile = new CandidateProfile();
//
//            candidateProfile.setCandidate(saved);
//
//            candidateRepository.save(candidateProfile);
//        }
//
//        // Create recruiter/company profile
//        if (role == Role.RECRUITER) {
//
//            CompanyProfile companyProfile = new CompanyProfile();
//
//            companyProfile.setRecruiter(saved);
//
//            companyProfile.setCompanyName(saved.getName());
//
//            recruiterRepository.save(companyProfile);
//        }
//
//        return new AuthResponse(
//                jwtService.generateToken(saved),
//                toUserResponse(saved)
//        );
//    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        Role role = request.role() == null
                ? Role.CANDIDATE
                : request.role();

        String otp = String.valueOf(
                (int)(Math.random() * 900000) + 100000
        );

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(
                passwordEncoder.encode(request.password())
        );
        user.setRole(role);

        user.setVerificationCode(otp);

        user.setVerified(false);

        User saved = userRepository.save(user);

        // Candidate profile
        if (role == Role.CANDIDATE) {

            CandidateProfile candidateProfile =
                    new CandidateProfile();

            candidateProfile.setCandidate(saved);

            candidateRepository.save(candidateProfile);
        }

        // Recruiter profile
        if (role == Role.RECRUITER) {

            CompanyProfile companyProfile =
                    new CompanyProfile();

            companyProfile.setRecruiter(saved);

            companyProfile.setCompanyName(
                    saved.getName()
            );

            recruiterRepository.save(companyProfile);
        }

        // Send OTP email
        emailService.sendOtp(
                saved.getEmail(),
                otp
        );

        // Do NOT login yet
        return new AuthResponse(
                null,
                toUserResponse(saved)
        );
    }

//    public AuthResponse login(LoginRequest request) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.email(), request.password())
//        );
//        User user = userRepository.findByEmail(request.email())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
//        return new AuthResponse(jwtService.generateToken(user), toUserResponse(user));
//    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        if (!user.isVerified()) {
            throw new IllegalArgumentException(
                    "Please verify your email first"
            );
        }

        return new AuthResponse(
                jwtService.generateToken(user),
                toUserResponse(user)
        );
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse verifyOtp(
            VerifyOtpRequest request
    ) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        if (!user.getVerificationCode()
                .equals(request.otp())) {

            throw new IllegalArgumentException(
                    "Invalid OTP"
            );
        }

        user.setVerified(true);

        user.setVerificationCode(null);

        userRepository.save(user);

        return new AuthResponse(
                jwtService.generateToken(user),
                toUserResponse(user)
        );
    }
}
