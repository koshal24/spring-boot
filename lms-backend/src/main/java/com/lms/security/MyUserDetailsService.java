package com.lms.security;

import com.lms.model.User;
import com.lms.repository.UserRepository;
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // If User has roles or a role field, map them. Adjust according to your User model.
        try {
            // Attempt to read a single role field
            java.lang.reflect.Method getRole = user.getClass().getMethod("getRole");
            Object roleObj = getRole.invoke(user);
            if (roleObj != null) {
                authorities.add(new SimpleGrantedAuthority(roleObj.toString()));
            }
        } catch (Exception ignored) {
            // fallback to roles list if available
            try {
                java.lang.reflect.Method getRoles = user.getClass().getMethod("getRoles");
                Object rolesObj = getRoles.invoke(user);
                if (rolesObj instanceof List) {
                    List<?> rolesList = (List<?>) rolesObj;
                    authorities.addAll(rolesList.stream().map(Object::toString).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                }
            } catch (Exception ignored2) {
                // no role information available - leave empty authorities
            }
        }

        if (authorities.isEmpty()) {
            authorities = Collections.emptyList();
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
