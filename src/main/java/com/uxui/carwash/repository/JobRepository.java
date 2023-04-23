package com.uxui.carwash.repository;

import com.uxui.carwash.model.CarType;
import com.uxui.carwash.model.Job;
import com.uxui.carwash.model.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    boolean existsByTypeAndCarType(JobType type, CarType carType);
}
