package com.uxui.carwash.repository;

import com.uxui.carwash.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a from Appointment a WHERE a.user.email = :email")
    Page<Appointment> findAllByEmail(@Param("email") String email, Pageable pageable);
}
