package com.uxui.carwash.bootstrap;

import com.uxui.carwash.model.*;
import com.uxui.carwash.model.security.Authority;
import com.uxui.carwash.model.security.User;
import com.uxui.carwash.repository.AppointmentRepository;
import com.uxui.carwash.repository.CarRepository;
import com.uxui.carwash.repository.EmployeeRepository;
import com.uxui.carwash.repository.JobRepository;
import com.uxui.carwash.repository.security.AuthorityRepository;
import com.uxui.carwash.repository.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;

@Component
@AllArgsConstructor
@Profile({"h2", "mysql"})
public class DataLoader implements CommandLineRunner {

    private AuthorityRepository authorityRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private CarRepository carRepository;
    private JobRepository jobRepository;
    private EmployeeRepository employeeRepository;
    private AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) {
        loadUserData();
    }

    @Transactional
    void loadUserData() {
        if (userRepository.count() == 0){
            Authority adminRole = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
            Authority clientRole = authorityRepository.save(Authority.builder().role("ROLE_CLIENT").build());

            UserDetails adminDetails = UserDetails.builder()
                    .firstName("admin")
                    .lastName("carwash")
                    .phoneNumber("0761111111")
                    .gender(Gender.MALE)
                    .build();

            User admin = User.builder()
                    .email("admin@carwash.com")
                    .password(passwordEncoder.encode("12345"))
                    .authority(adminRole)
                    .userDetails(adminDetails)
                    .build();

            UserDetails client1Details = UserDetails.builder()
                    .firstName("client")
                    .lastName("carwash")
                    .phoneNumber("0751111111")
                    .gender(Gender.MALE)
                    .build();

            User client1 = User.builder()
                    .email("client@carwash.com")
                    .password(passwordEncoder.encode("12345"))
                    .authority(clientRole)
                    .userDetails(client1Details)
                    .build();

            UserDetails client2Details = UserDetails.builder()
                    .firstName("client")
                    .lastName("carwash")
                    .phoneNumber("0751111111")
                    .gender(Gender.MALE)
                    .build();

            User client2 = User.builder()
                    .email("client1@carwash.com")
                    .password(passwordEncoder.encode("12345"))
                    .authority(clientRole)
                    .userDetails(client2Details)
                    .build();

            userRepository.save(admin);
            User savedClient1 = userRepository.save(client1);
            User savedClient2 = userRepository.save(client2);

            Car car1 = Car.builder()
                    .licensePlate("B222ABC")
                    .type(CarType.VAN)
                    .createdAt(LocalDateTime.now())
                    .user(savedClient1)
                    .build();

            Car car2 = Car.builder()
                    .licensePlate("B111DEF")
                    .type(CarType.REGULAR)
                    .createdAt(LocalDateTime.now())
                    .user(savedClient2)
                    .build();

            carRepository.save(car1);
            carRepository.save(car2);

            Job job1 = Job.builder()
                    .type(JobType.INTERIOR)
                    .durationMinutes(25L)
                    .price(20.0)
                    .carType(CarType.REGULAR)
                    .numberOfEmployees(1)
                    .build();

            Job job2 = Job.builder()
                    .type(JobType.EXTERIOR)
                    .durationMinutes(25L)
                    .price(20.0)
                    .carType(CarType.REGULAR)
                    .numberOfEmployees(1)
                    .build();

            Job job3 = Job.builder()
                    .type(JobType.INTERIOR_AND_EXTERIOR)
                    .durationMinutes(50L)
                    .price(35.0)
                    .carType(CarType.REGULAR)
                    .numberOfEmployees(2)
                    .build();

            Job job4 = Job.builder()
                    .type(JobType.EXTERIOR)
                    .durationMinutes(55L)
                    .price(40.0)
                    .carType(CarType.VAN)
                    .numberOfEmployees(2)
                    .build();

            Job savedJob1 = jobRepository.save(job1);
            Job savedJob2 = jobRepository.save(job2);
            Job savedJob3 = jobRepository.save(job3);
            Job savedJob4 = jobRepository.save(job4);

            Employee employee1 = Employee.builder()
                    .firstName("Avram")
                    .lastName("Ionescu")
                    .email("aionescu@carwash.com")
                    .phoneNumber("1234567895")
                    .salary(2000.0)
                    .hireDate(LocalDate.now())
                    .build();

            Employee employee2 = Employee.builder()
                    .firstName("George")
                    .lastName("Georgescu")
                    .email("ggeorgescu@carwash.com")
                    .phoneNumber("1334587895")
                    .salary(2200.0)
                    .hireDate(LocalDate.now())
                    .build();

            Employee employee3 = Employee.builder()
                    .firstName("Gabriel")
                    .lastName("Enescu")
                    .email("genescu@carwash.com")
                    .phoneNumber("2335587855")
                    .salary(2100.0)
                    .hireDate(LocalDate.now())
                    .build();

            Employee employee4 = Employee.builder()
                    .firstName("Mihai")
                    .lastName("Prajescu")
                    .email("mprajescu@carwash.com")
                    .phoneNumber("0235589855")
                    .salary(2000.0)
                    .hireDate(LocalDate.now())
                    .build();

            Employee savedEmployee1 = employeeRepository.save(employee1);
            Employee savedEmployee2 = employeeRepository.save(employee2);
            Employee savedEmployee3 = employeeRepository.save(employee3);
            Employee savedEmployee4 = employeeRepository.save(employee4);

            Appointment appointment1 = Appointment.builder()
                    .startTime(LocalDateTime.now().plus(Duration.of(1, HOURS)))
                    .job(savedJob1)
                    .car(car2)
                    .user(savedClient2)
                    .build();

            Appointment appointment2 = Appointment.builder()
                    .startTime(LocalDateTime.now().plus(Duration.of(3, HOURS)))
                    .job(savedJob4)
                    .car(car1)
                    .user(savedClient1)
                    .build();

            Appointment appointment3 = Appointment.builder()
                    .startTime(LocalDateTime.now().plus(Duration.of(4, HOURS)))
                    .job(savedJob2)
                    .car(car1)
                    .user(savedClient1)
                    .build();

            Appointment appointment4 = Appointment.builder()
                    .startTime(LocalDateTime.now().plus(Duration.of(6, HOURS)))
                    .job(savedJob3)
                    .car(car1)
                    .user(savedClient1)
                    .build();

            Appointment savedAppointment1 = appointmentRepository.save(appointment1);
            Appointment savedAppointment2 = appointmentRepository.save(appointment2);
            Appointment savedAppointment3 = appointmentRepository.save(appointment3);
            Appointment savedAppointment4 = appointmentRepository.save(appointment4);

            savedAppointment1.setEmployees(List.of(savedEmployee1, savedEmployee2));
            savedAppointment2.setEmployees(List.of(savedEmployee3));
            savedAppointment3.setEmployees(List.of(savedEmployee1, savedEmployee2));
            savedAppointment4.setEmployees(List.of(savedEmployee3, savedEmployee4));

            appointmentRepository.save(savedAppointment1);
            appointmentRepository.save(savedAppointment2);
            appointmentRepository.save(savedAppointment3);
            appointmentRepository.save(savedAppointment4);
        }
    }
}
