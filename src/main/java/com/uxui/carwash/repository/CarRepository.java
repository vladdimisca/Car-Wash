package com.uxui.carwash.repository;

import com.uxui.carwash.model.Car;
import com.uxui.carwash.model.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByLicensePlateAndUser_Id(String licensePlate, Long userId);

    @Query("SELECT c from Car c WHERE c.user.email = :email")
    List<Car> findAllByEmail(@Param("email") String email);
}
