package edu.ncsu.csc.itrust2.services;

import edu.ncsu.csc.itrust2.repositories.PatientRepository;
import edu.ncsu.csc.itrust2.repositories.UserRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class PatientService extends UserService {

    private final PatientRepository repository;

    public PatientService(UserRepository userRepository, PatientRepository repository) {
        super(userRepository);
        this.repository = repository;
    }

    @Override
    protected JpaRepository getRepository() {
        return repository;
    }
}
