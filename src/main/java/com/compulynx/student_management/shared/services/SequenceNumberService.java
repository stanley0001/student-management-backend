package com.compulynx.student_management.shared.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SequenceNumberService {
    public int getNextSequenceNumber(JpaRepository<?, ?> repository) {
        return (int) (repository.count() + 1);
    }
}
