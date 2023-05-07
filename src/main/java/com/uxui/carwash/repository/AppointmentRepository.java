package com.uxui.carwash.repository;

import com.uxui.carwash.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a from Appointment a WHERE a.user.email = :email")
    List<Appointment> findAllByEmail(@Param("email") String email);

    @Query("SELECT a from Appointment a JOIN a.employees e WHERE e.user.email = :email")
    List<Appointment> findAllByEmployeeEmail(@Param("email") String email);
}
