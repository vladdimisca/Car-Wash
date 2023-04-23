package com.uxui.carwash.service;

import com.uxui.carwash.error.exception.ConflictException;
import com.uxui.carwash.error.exception.ResourceNotFoundException;
import com.uxui.carwash.model.CarType;
import com.uxui.carwash.model.Job;
import com.uxui.carwash.model.JobType;
import com.uxui.carwash.repository.JobRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    private static final Long ID = 1L;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    @DisplayName("Create job - success")
    void create_success() {
        Job job = getJob();
        Job savedJob = getSavedJob();

        when(jobRepository.existsByTypeAndCarType(job.getType(), job.getCarType())).thenReturn(false);
        when(jobRepository.save(job)).thenReturn(savedJob);

        Job resultedJob = jobService.create(job);

        assertNotNull(resultedJob);
        assertEquals(savedJob, resultedJob);
        verify(jobRepository, times(1)).existsByTypeAndCarType(job.getType(), job.getCarType());
        verify(jobRepository, times(1)).save(job);
    }

    @Test
    @DisplayName("Create job - existing type and car type - failure")
    void create_existingTypeAndCarType_failure() {
        Job job = getJob();

        when(jobRepository.existsByTypeAndCarType(job.getType(), job.getCarType())).thenReturn(true);

        assertThrows(ConflictException.class, () -> jobService.create(job));
        verify(jobRepository, times(1)).existsByTypeAndCarType(job.getType(), job.getCarType());
        verify(jobRepository, never()).save(job);
    }

    @Test
    @DisplayName("Get job by id - success")
    void getById_success() {
        Job job = getSavedJob();

        when(jobRepository.findById(ID)).thenReturn(Optional.of(job));

        Job resultedJob = jobService.getById(ID);

        assertNotNull(resultedJob);
        assertEquals(job, resultedJob);
        verify(jobRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Get job by id - not found - failure")
    void getById_notFound_failure() {
        when(jobRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobService.getById(ID));
        verify(jobRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Get all jobs - success")
    void getAll_success() {
        when(jobRepository.findAll()).thenReturn(List.of(getSavedJob()));

        List<Job> jobs = jobService.getAll();

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
        verify(jobRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Update job by id - success")
    void update_success() {
        Job job = getSavedJob();
        Job updatedJob = getSavedJob();
        updatedJob.setDurationMinutes(5L);
        updatedJob.setPrice(25.5);
        updatedJob.setType(JobType.INTERIOR_AND_EXTERIOR);

        when(jobRepository.existsByTypeAndCarType(updatedJob.getType(), updatedJob.getCarType())).thenReturn(false);
        when(jobRepository.findById(ID)).thenReturn(Optional.of(job));
        when(jobRepository.save(job)).thenReturn(updatedJob);

        Job resultedJob = jobService.update(ID, updatedJob);

        assertNotNull(resultedJob);
        assertEquals(updatedJob, resultedJob);
        verify(jobRepository, times(1)).save(job);
        verify(jobRepository, times(1))
                .existsByTypeAndCarType(updatedJob.getType(), updatedJob.getCarType());
    }

    @Test
    @DisplayName("Delete job by id - success")
    void delete_success() {
        Job job = getSavedJob();

        when(jobRepository.findById(ID)).thenReturn(Optional.of(job));

        jobService.deleteById(ID);

        verify(jobRepository, times(1)).findById(ID);
        verify(jobRepository, times(1)).delete(job);
    }

    private Job getJob() {
        Job job = new Job();
        job.setType(JobType.EXTERIOR);
        job.setCarType(CarType.MINIBUS);
        job.setPrice(15.0);
        job.setNumberOfEmployees(5);
        job.setDurationMinutes(15L);

        return job;
    }

    private Job getSavedJob() {
        Job savedJob = getJob();
        savedJob.setId(ID);

        return savedJob;
    }
}
