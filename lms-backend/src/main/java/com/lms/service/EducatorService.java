package com.lms.service;

import com.lms.model.Educator;
import com.lms.repository.EducatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EducatorService {
    @Autowired
    private EducatorRepository educatorRepository;

    public List<Educator> getAllEducators() {
        return educatorRepository.findAll();
    }

    public Optional<Educator> getEducatorById(String id) {
        return educatorRepository.findById(id);
    }

    public Optional<Educator> getEducatorByEmail(String email) {
        return educatorRepository.findByEmail(email);
    }

    public Educator saveEducator(Educator e) {
        return educatorRepository.save(e);
    }

    public void deleteEducator(String id) {
        educatorRepository.deleteById(id);
    }
}
