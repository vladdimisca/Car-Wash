package com.uxui.carwash.service;

import com.uxui.carwash.error.exception.ConflictException;
import com.uxui.carwash.error.exception.ForbiddenActionException;
import com.uxui.carwash.error.exception.ResourceNotFoundException;
import com.uxui.carwash.model.Car;
import com.uxui.carwash.model.CarType;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.repository.CarRepository;
import com.uxui.carwash.service.security.JpaUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    private static final Long ID = 1L;
    private static final String USER_EMAIL = "test";

    @Mock
    private UserService userService;

    @Mock
    private JpaUserDetailsService jpaUserDetailsService;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    @Test
    @DisplayName("Create car - success")
    void create_success() {
        Car car = getCar();
        Car savedCar = getSavedCar();
        User user = getUser();

        when(userService.getByEmail(USER_EMAIL)).thenReturn(user);
        when(jpaUserDetailsService.getCurrentUserPrincipal())
                .thenReturn(new org.springframework.security.core.userdetails.User(USER_EMAIL, "pass", new HashSet<>()));
        when(carRepository.existsByLicensePlateAndUser_Id(car.getLicensePlate(), user.getId())).thenReturn(false);
        when(carRepository.save(car)).thenReturn(savedCar);

        Car resultedCar = carService.create(car);

        assertNotNull(resultedCar);
        assertEquals(savedCar.getId(), resultedCar.getId());
        assertEquals(savedCar.getLicensePlate(), resultedCar.getLicensePlate());
        verify(carRepository, times(1))
                .existsByLicensePlateAndUser_Id(car.getLicensePlate(), user.getId());
    }

    @Test
    @DisplayName("Create car - existing license plate and user - success")
    void create_existingLicensePlateAndUser_failure() {
        Car car = getCar();
        User user = getUser();

        when(userService.getByEmail(USER_EMAIL)).thenReturn(user);
        when(jpaUserDetailsService.getCurrentUserPrincipal()).thenReturn(
                new org.springframework.security.core.userdetails.User(USER_EMAIL, "pass", new HashSet<>()));
        when(carRepository.existsByLicensePlateAndUser_Id(car.getLicensePlate(), user.getId())).thenReturn(true);

        assertThrows(ConflictException.class, () -> carService.create(car));
        verify(carRepository, times(1)).existsByLicensePlateAndUser_Id(car.getLicensePlate(), user.getId());
        verify(carRepository, never()).save(car);
    }

    @Test
    @DisplayName("Get car by id - success")
    void getById_success() {
        Car car = getSavedCar();

        when(carRepository.findById(ID)).thenReturn(Optional.of(car));
        when(jpaUserDetailsService.hasAuthority("ROLE_ADMIN")).thenReturn(true);

        Car resultedCar = carService.getById(ID);

        assertNotNull(resultedCar);
        assertEquals(car.getId(), resultedCar.getId());
        assertEquals(car.getLicensePlate(), resultedCar.getLicensePlate());
        verify(carRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Get car by id - forbidden - failure")
    void getById_forbidden_failure() {
        Car car = getSavedCar();

        when(carRepository.findById(ID)).thenReturn(Optional.of(car));
        when(jpaUserDetailsService.hasAuthority("ROLE_ADMIN")).thenReturn(false);
        when(jpaUserDetailsService.getCurrentUserPrincipal()).thenReturn(
                new org.springframework.security.core.userdetails.User("random@email", "pass", new HashSet<>()));

        assertThrows(ForbiddenActionException.class, () -> carService.getById(ID));

        verify(carRepository, times(1)).findById(ID);
        verify(jpaUserDetailsService, times(1)).hasAuthority("ROLE_ADMIN");
        verify(jpaUserDetailsService, times(1)).getCurrentUserPrincipal();
    }

    @Test
    @DisplayName("Get car by id - not found - failure")
    void getById_notFound_failure() {
        when(carRepository.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carService.getById(ID));

        verify(carRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("Get all cars - success")
    void getAll_success() {
        when(carRepository.findAll()).thenReturn(List.of(getSavedCar()));
        when(jpaUserDetailsService.hasAuthority("ROLE_ADMIN")).thenReturn(true);

        List<Car> cars = carService.getAll();

        assertNotNull(cars);
        assertEquals(1, cars.size());
        verify(carRepository, times(1)).findAll();
        verify(jpaUserDetailsService, times(1)).hasAuthority("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Get all cars by user - success")
    void getAllByUser_success() {
        when(carRepository.findAllByEmail(USER_EMAIL)).thenReturn(List.of(getSavedCar()));
        when(jpaUserDetailsService.hasAuthority("ROLE_ADMIN")).thenReturn(false);
        when(jpaUserDetailsService.getCurrentUserPrincipal()).thenReturn(
                new org.springframework.security.core.userdetails.User(USER_EMAIL, "pass", new HashSet<>()));

        List<Car> cars = carService.getAll();

        assertNotNull(cars);
        assertEquals(1, cars.size());
        verify(carRepository, times(1)).findAllByEmail(USER_EMAIL);
        verify(jpaUserDetailsService, times(1)).hasAuthority("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Update car by id - success")
    void update_success() {
        Car car = getSavedCar();
        Car updatedCar = getSavedCar();
        updatedCar.setType(CarType.VAN);
        updatedCar.setLicensePlate("test_new");

        when(carRepository.findById(ID)).thenReturn(Optional.of(car));
        when(jpaUserDetailsService.hasAuthority("ROLE_ADMIN")).thenReturn(false);
        when(jpaUserDetailsService.getCurrentUserPrincipal()).thenReturn(
                new org.springframework.security.core.userdetails.User(USER_EMAIL, "pass", new HashSet<>()));
        when(carRepository.existsByLicensePlateAndUser_Id(updatedCar.getLicensePlate(), updatedCar.getUser().getId())).thenReturn(false);
        when(carRepository.save(car)).thenReturn(updatedCar);

        Car resultedCar = carService.update(ID, updatedCar);

        assertNotNull(resultedCar);
        verify(carRepository, times(1)).findById(ID);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    @DisplayName("Delete car by id - success")
    void delete_success() {
        Car car = getSavedCar();

        when(jpaUserDetailsService.hasAuthority("ROLE_ADMIN")).thenReturn(true);
        when(carRepository.findById(ID)).thenReturn(Optional.of(car));

        carService.deleteById(ID);

        verify(carRepository, times(1)).findById(ID);
        verify(carRepository, times(1)).delete(car);
    }

    private Car getCar() {
        Car car = new Car();
        car.setType(CarType.REGULAR);
        car.setCreatedAt(LocalDateTime.now());
        car.setLicensePlate("test");
        car.setUser(getUser());

        return car;
    }

    private Car getSavedCar() {
        Car savedCar = getCar();
        savedCar.setId(ID);

        return savedCar;
    }

    private User getUser() {
        return User.builder()
                .email(USER_EMAIL)
                .build();
    }
}
