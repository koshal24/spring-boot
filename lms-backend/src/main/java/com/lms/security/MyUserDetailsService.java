package com.lms.security;

import com.lms.model.User;
import com.lms.model.Admin;
import com.lms.repository.UserRepository;
import com.lms.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String foundEmail = null;
        String password = null;
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Admins stored separately (if present)
        var adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            foundEmail = adminOpt.get().getEmail();
            password = adminOpt.get().getPassword();
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }

        // Users (students, educators) - single collection with role enum
        if (foundEmail == null) {
            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                foundEmail = userOpt.get().getEmail();
                password = userOpt.get().getPassword();
                String role = userOpt.get().getRole() != null ? userOpt.get().getRole().name() : "STUDENT";
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        if (foundEmail == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(foundEmail)
                .password(password)
                .authorities(authorities)
                .build();
    }
}
