package com.lms.security;

import com.lms.model.User;
import com.lms.repository.UserRepository;
import com.lms.repository.EducatorRepository;
import com.lms.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EducatorRepository educatorRepository;
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Look up user across student, educator, admin collections and assign authority accordingly
        String foundEmail = null;
        String password = null;
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Admins
        var adminOpt = adminRepository.findAll().stream().filter(a -> a.getEmail().equals(email)).findFirst();
        if (adminOpt.isPresent()) {
            foundEmail = adminOpt.get().getEmail();
            password = adminOpt.get().getPassword();
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }

        // Educators
        if (foundEmail == null) {
            var edOpt = educatorRepository.findAll().stream().filter(e -> e.getEmail().equals(email)).findFirst();
            if (edOpt.isPresent()) {
                foundEmail = edOpt.get().getEmail();
                password = edOpt.get().getPassword();
                authorities.add(new SimpleGrantedAuthority("EDUCATOR"));
            }
        }

        // Students
        if (foundEmail == null) {
            var userOpt = userRepository.findAll().stream().filter(u -> u.getEmail().equals(email)).findFirst();
            if (userOpt.isPresent()) {
                foundEmail = userOpt.get().getEmail();
                password = userOpt.get().getPassword();
                authorities.add(new SimpleGrantedAuthority("STUDENT"));
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
