package com.lms.controller;

import com.lms.model.User;
import com.lms.model.Educator;
import com.lms.model.Admin;
import com.lms.repository.UserRepository;
import com.lms.repository.EducatorRepository;
import com.lms.repository.AdminRepository;
import com.lms.security.JwtUtil;
import com.lms.security.MyUserDetailsService;
import com.lms.dto.LoginRequest;
import com.lms.dto.RegisterRequest;
import com.lms.dto.VerifyRequest;
import com.lms.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EducatorRepository educatorRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        Map<String, Object> response = new HashMap<>();
        try {
            // Find the user across Admin, Educator, and Student collections
            Admin admin = adminRepository.findAll().stream().filter(a -> a.getEmail().equals(email)).findFirst().orElse(null);
            Educator educator = null;
            User student = null;
            String role = null;

            if (admin != null) {
                role = "ADMIN";
            } else {
                educator = educatorRepository.findAll().stream().filter(e -> e.getEmail().equals(email)).findFirst().orElse(null);
                if (educator != null) role = "EDUCATOR";
                else {
                    student = userRepository.findAll().stream().filter(u -> u.getEmail().equals(email)).findFirst().orElse(null);
                    if (student != null) role = "STUDENT";
                }
            }

            if (role == null) {
                response.put("error", "Invalid credentials");
                return ResponseEntity.status(401).body(response);
            }

            // For student and educator, require email verification
            if (("STUDENT".equals(role) && (student == null || !student.isVerified())) || ("EDUCATOR".equals(role) && (educator == null || !educator.isVerified()))) {
                response.put("error", "Email not verified");
                return ResponseEntity.status(403).body(response);
            }

            // Authenticate using configured AuthenticationManager which will consult UserDetailsService
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            final String jwt = jwtUtil.generateToken(userDetails.getUsername(), role);
            response.put("token", jwt);
            response.put("role", role);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        // ensure the email is not already used in any user-type collection
        boolean exists = userRepository.findAll().stream().anyMatch(u -> u.getEmail().equals(request.getEmail()))
                || educatorRepository.findAll().stream().anyMatch(e -> e.getEmail().equals(request.getEmail()))
                || adminRepository.findAll().stream().anyMatch(a -> a.getEmail().equals(request.getEmail()));
        if (exists) {
            response.put("error", "Email already exists");
            return ResponseEntity.status(409).body(response);
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Registration only creates students. Role field is ignored or must be STUDENT.
        String role = request.getRole();
        if (role != null && !role.isBlank() && !"STUDENT".equalsIgnoreCase(role)) {
            response.put("error", "Cannot self-assign roles other than STUDENT during registration");
            return ResponseEntity.status(403).body(response);
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);
        // generate verification code
        String code = String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit code
        user.setVerificationCode(code);
        user.setVerificationExpiry(java.time.Instant.now().plusSeconds(15 * 60)); // 15 minutes
        userRepository.save(user);

        // send verification code
        emailService.sendVerificationCode(user.getEmail(), code);

        response.put("message", "Registration pending verification. Check your email for the code.");
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyEmail(@Valid @RequestBody VerifyRequest request) {
        Map<String, Object> response = new HashMap<>();
        User user = userRepository.findAll().stream().filter(u -> u.getEmail().equals(request.getEmail())).findFirst().orElse(null);
        if (user == null) {
            response.put("error", "User not found");
            return ResponseEntity.status(404).body(response);
        }
        if (user.isVerified()) {
            response.put("message", "Already verified");
            return ResponseEntity.ok(response);
        }
        if (user.getVerificationCode() == null || user.getVerificationExpiry() == null) {
            response.put("error", "No verification code present. Please register again.");
            return ResponseEntity.status(400).body(response);
        }
        if (user.getVerificationExpiry().isBefore(java.time.Instant.now())) {
            response.put("error", "Verification code expired");
            return ResponseEntity.status(400).body(response);
        }
        if (!user.getVerificationCode().equals(request.getCode())) {
            response.put("error", "Invalid verification code");
            return ResponseEntity.status(400).body(response);
        }
        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationExpiry(null);
        userRepository.save(user);
        response.put("message", "Email verified successfully");
        return ResponseEntity.ok(response);
    }
}