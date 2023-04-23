package com.uxui.carwash.service;

import com.uxui.carwash.error.ErrorMessage;
import com.uxui.carwash.error.exception.ConflictException;
import com.uxui.carwash.error.exception.ResourceNotFoundException;
import com.uxui.carwash.model.Job;
import com.uxui.carwash.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job create(Job job) {
        checkJobNotExisting(job);
        return jobRepository.save(job);
    }

    public Job update(Long id, Job job) {
        Job existingJob = getById(id);
        if (!existingJob.getType().equals(job.getType()) || !existingJob.getCarType().equals(job.getCarType())) {
            checkJobNotExisting(job);
        }

        copyValues(existingJob, job);

        return jobRepository.save(existingJob);
    }

    public Job getById(Long id) {
        return jobRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND, "job", id));
    }

    public List<Job> getAll() {
        return jobRepository.findAll();
    }

    public void deleteById(Long id) {
        Job job = getById(id);
        jobRepository.delete(job);
    }

    private void checkJobNotExisting(Job job) {
        if (jobRepository.existsByTypeAndCarType(job.getType(), job.getCarType())) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "job", "type and car type");
        }
    }

    private void copyValues(Job to, Job from) {
        to.setType(from.getType());
        to.setDurationMinutes(from.getDurationMinutes());
        to.setCarType(from.getCarType());
        to.setPrice(from.getPrice());
        to.setNumberOfEmployees(from.getNumberOfEmployees());
    }
}
