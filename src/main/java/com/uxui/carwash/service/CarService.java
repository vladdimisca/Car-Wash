package com.uxui.carwash.service;

import com.uxui.carwash.error.ErrorMessage;
import com.uxui.carwash.error.exception.ConflictException;
import com.uxui.carwash.error.exception.ForbiddenActionException;
import com.uxui.carwash.error.exception.ResourceNotFoundException;
import com.uxui.carwash.model.Car;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.repository.CarRepository;
import com.uxui.carwash.service.security.JpaUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private final CarRepository carRepository;
    private final JpaUserDetailsService jpaUserDetailsService;
    private final UserService userService;

    public Car create(Car car) {
        User user = userService.getByEmail(jpaUserDetailsService.getCurrentUserPrincipal().getUsername());
        checkCarNotExisting(car, user);

        car.setUser(user);
        car.setCreatedAt(LocalDateTime.now());
        return carRepository.save(car);
    }

    public Car update(Long id, Car car) {
        Car existingCar = getById(id);
        if (!existingCar.getUser().getEmail().equals(jpaUserDetailsService.getCurrentUserPrincipal().getUsername())) {
            throw new ForbiddenActionException(ErrorMessage.FORBIDDEN);
        }
        if (!existingCar.getLicensePlate().equals(car.getLicensePlate())) {
            checkCarNotExisting(car, car.getUser());
        }

        copyValues(existingCar, car);

        return carRepository.save(existingCar);
    }

    public Car getById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND, "car", id));
        boolean isAdmin = jpaUserDetailsService.hasAuthority("ROLE_ADMIN");
        if (!isAdmin && !car.getUser().getEmail().equals(jpaUserDetailsService.getCurrentUserPrincipal().getUsername())) {
            throw new ForbiddenActionException(ErrorMessage.FORBIDDEN);
        }

        return car;
    }

    public List<Car> getAll() {
        boolean isAdmin = jpaUserDetailsService.hasAuthority("ROLE_ADMIN");
        if (isAdmin) {
            return carRepository.findAll();
        }
        return carRepository.findAllByEmail(jpaUserDetailsService.getCurrentUserPrincipal().getUsername());
    }

    public void deleteById(Long id) {
        Car car = getById(id);
        carRepository.delete(car);
    }

    private void checkCarNotExisting(Car car, User user) {
        if (carRepository.existsByLicensePlateAndUser(car.getLicensePlate(), user)) {
            throw new ConflictException(ErrorMessage.ALREADY_EXISTS, "car", "license plate");
        }
    }

    private void copyValues(Car to, Car from) {
        to.setType(from.getType());
        to.setLicensePlate(from.getLicensePlate());
    }
}
