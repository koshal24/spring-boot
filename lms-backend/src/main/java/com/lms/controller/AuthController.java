package com.lms.controller;

import com.lms.model.User;
import com.lms.model.Admin;
import com.lms.model.Role;
import com.lms.repository.UserRepository;
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
            // Find account: admin first, otherwise check users collection for role
            Admin admin = adminRepository.findByEmail(email).orElse(null);
            User student = null;
            String role = null;

            if (admin != null) {
                role = Role.ADMIN.name();
            } else {
                student = userRepository.findByEmail(email).orElse(null);
                if (student != null) role = student.getRole() != null ? student.getRole().name() : Role.STUDENT.name();
            }

            if (role == null) {
                response.put("error", "Invalid credentials");
                return ResponseEntity.status(401).body(response);
            }

            // require verification for students and educators (stored on User)
            if ((Role.STUDENT.name().equals(role) && (student == null || !student.isVerified())) || (Role.EDUCATOR.name().equals(role) && (student == null || !student.isVerified()))) {
                response.put("error", "Email not verified");
                return ResponseEntity.status(403).body(response);
            }
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
    boolean exists = userRepository.findByEmail(request.getEmail()).isPresent()
        || adminRepository.findByEmail(request.getEmail()).isPresent();
    if (exists) {
            response.put("error", "Email already exists");
            return ResponseEntity.status(409).body(response);
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Registration creates STUDENT accounts only. Ignore role in request.
        user.setRole(Role.STUDENT);
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

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        Map<String, Object> resp = new HashMap<>();
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
        }
        String token = authHeader.substring(7);
        String email;
        try {
            email = jwtUtil.extractUsername(token);
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
        // find which account type
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            resp.put("id", admin.getId());
            resp.put("email", admin.getEmail());
            resp.put("name", admin.getName());
            resp.put("role", Role.ADMIN.name());
            return ResponseEntity.ok(resp);
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            resp.put("id", user.getId());
            resp.put("email", user.getEmail());
            resp.put("name", user.getName());
            resp.put("role", user.getRole() != null ? user.getRole().name() : Role.STUDENT.name());
            resp.put("verified", user.isVerified());
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(404).body(Map.of("error", "User not found"));
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