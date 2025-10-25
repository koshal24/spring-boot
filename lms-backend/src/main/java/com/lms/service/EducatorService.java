package com.lms.service;

import com.lms.model.User;
import com.lms.model.Role;
import com.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EducatorService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllEducators() {
        return userRepository.findByRole(Role.EDUCATOR);
    }

    public Optional<User> getEducatorById(String id) {
        return userRepository.findById(id).filter(u -> u.getRole() == Role.EDUCATOR);
    }

    public Optional<User> getEducatorByEmail(String email) {
        return userRepository.findByEmail(email).filter(u -> u.getRole() == Role.EDUCATOR);
    }

    public User saveEducator(User e) {
        // ensure role is set to EDUCATOR
        e.setRole(Role.EDUCATOR);
        return userRepository.save(e);
    }

    public void deleteEducator(String id) {
        // Deprecated operation: deleting educators is not recommended. Prefer demote to STUDENT:
        userRepository.findById(id).ifPresent(u -> {
            u.setRole(Role.STUDENT);
            userRepository.save(u);
        });
    }
}
