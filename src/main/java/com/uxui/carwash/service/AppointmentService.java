package com.uxui.carwash.service;

import com.uxui.carwash.error.ErrorMessage;
import com.uxui.carwash.error.exception.BadRequestException;
import com.uxui.carwash.error.exception.ConflictException;
import com.uxui.carwash.error.exception.ForbiddenActionException;
import com.uxui.carwash.error.exception.ResourceNotFoundException;
import com.uxui.carwash.model.Appointment;
import com.uxui.carwash.model.Car;
import com.uxui.carwash.model.Employee;
import com.uxui.carwash.model.Job;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.repository.AppointmentRepository;
import com.uxui.carwash.service.security.JpaUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final JpaUserDetailsService jpaUserDetailsService;
    private final UserService userService;
    private final JobService jobService;
    private final CarService carService;
    private final EmployeeService employeeService;

    public Appointment create(Appointment appointment) {
        Job job = jobService.getById(appointment.getJob().getId());
        Car car = carService.getById(appointment.getCar().getId());
        if (!car.getType().equals(job.getCarType())) {
            throw new BadRequestException(ErrorMessage.NOT_MATCHING, "Car type", "job");
        }

        List<Employee> employees =
                getFreeEmployees(job.getNumberOfEmployees(), appointment.getStartTime(), job.getDurationMinutes());

        User user = userService.getByEmail(jpaUserDetailsService.getCurrentUserPrincipal().getUsername());

        appointment.setEmployees(employees);
        appointment.setUser(user);
        appointment.setCar(car);
        appointment.setJob(job);

        return appointmentRepository.save(appointment);
    }

    public Appointment update(Long id, Appointment appointment) {
        Appointment existingAppointment = getById(id);
        if (!existingAppointment.getUser().getEmail().equals(jpaUserDetailsService.getCurrentUserPrincipal().getUsername())) {
            throw new ForbiddenActionException(ErrorMessage.FORBIDDEN);
        }

        Car car = carService.getById(appointment.getCar().getId());
        Job job = jobService.getById(appointment.getJob().getId());
        if (!car.getType().equals(job.getCarType())) {
            throw new BadRequestException(ErrorMessage.NOT_MATCHING, "car type", "job");
        }

        Appointment[] obsoleteAppointments = existingAppointment.getEmployees().stream()
                .flatMap(employee -> employee.getAppointments().stream())
                .toArray(Appointment[]::new);

        List<Employee> employees = getFreeEmployees(
                job.getNumberOfEmployees(), appointment.getStartTime(), job.getDurationMinutes(), obsoleteAppointments);

        existingAppointment.setEmployees(employees);
        existingAppointment.setStartTime(appointment.getStartTime());
        existingAppointment.setCar(car);
        existingAppointment.setJob(job);

        return appointmentRepository.save(existingAppointment);
    }


    public Appointment getById(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND, "appointment", id));
        boolean isAdmin = jpaUserDetailsService.hasAuthority("ROLE_ADMIN");
        if (!isAdmin && !appointment.getUser().getEmail().equals(jpaUserDetailsService.getCurrentUserPrincipal().getUsername())) {
            throw new ForbiddenActionException(ErrorMessage.FORBIDDEN);
        }

        return appointment;
    }


    public Page<Appointment> getAll(Pageable pageable) {
        boolean isAdmin = jpaUserDetailsService.hasAuthority("ROLE_ADMIN");
        if (isAdmin) {
            return appointmentRepository.findAll(pageable);
        }
        return appointmentRepository.findAllByEmail(jpaUserDetailsService.getCurrentUserPrincipal().getUsername(), pageable);
    }

    public void deleteById(Long id) {
        Appointment appointment = getById(id);
        appointmentRepository.delete(appointment);
    }

    private List<Employee> getFreeEmployees(Integer numberOfEmployees, LocalDateTime startTime,
                                            Long durationMinutes, Appointment... obsoleteAppointments) {
        List<Employee> freeEmployees = new ArrayList<>();
        List<Employee> allEmployees = employeeService.getAll();
        for (Employee employee : allEmployees) {
            if (numberOfEmployees.equals(freeEmployees.size())) {
                return freeEmployees;
            }

            boolean isFree = true;
            List<Appointment> appointments = employee.getAppointments();
            appointments.removeAll(Arrays.asList(obsoleteAppointments));
            for (Appointment appointment : appointments) {
                LocalDateTime start = appointment.getStartTime().plusMinutes(1);
                LocalDateTime end = appointment.getStartTime().plusMinutes(appointment.getJob().getDurationMinutes() - 1);
                if ((start.isAfter(startTime) && start.isBefore(startTime.plusMinutes(durationMinutes))) ||
                        (end.isAfter(startTime) && end.isBefore(startTime.plusMinutes(durationMinutes)))) {
                    isFree = false;
                    break;
                }
            }
            if (isFree) {
                freeEmployees.add(employee);
            }
        }
        if (freeEmployees.size() < numberOfEmployees) {
            throw new ConflictException(ErrorMessage.NOT_ENOUGH_RESOURCES, "employees");
        }
        return freeEmployees;
    }
}
