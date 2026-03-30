package com.tinhnguyenxanh.config;

import com.tinhnguyenxanh.entity.User;
import com.tinhnguyenxanh.enums.Role;
import com.tinhnguyenxanh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Tạo tài khoản Admin nếu chưa có
        if (!userRepo.existsByEmail("admin@tinhnguyenxanh.vn")) {
            User admin = User.builder()
                    .fullName("Quản trị viên")
                    .email("admin@tinhnguyenxanh.vn")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .registeredDate(LocalDateTime.now())
                    .enabled(true)
                    .accountLocked(false)
                    .build();
            userRepo.save(admin);
            System.out.println("[INIT] Tài khoản Admin đã được tạo: admin@tinhnguyenxanh.vn / Admin@123");
        }
    }
}
