package com.tinhnguyenxanh.service;

import com.tinhnguyenxanh.dto.RegisterDTO;
import com.tinhnguyenxanh.entity.User;
import com.tinhnguyenxanh.entity.Volunteer;
import com.tinhnguyenxanh.enums.Role;
import com.tinhnguyenxanh.repository.UserRepository;
import com.tinhnguyenxanh.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final VolunteerRepository volunteerRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean register(RegisterDTO dto) {
        if (userRepo.existsByEmail(dto.getEmail())) return false;
        if (!dto.getPassword().equals(dto.getConfirmPassword())) return false;

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .role(Role.VOLUNTEER)
                .registeredDate(LocalDateTime.now())
                .enabled(true)
                .build();

        userRepo.save(user);

        // Tự động tạo Volunteer profile
        Volunteer volunteer = Volunteer.builder()
                .user(user)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhoneNumber())
                .joinedDate(LocalDateTime.now())
                .availability("Available")
                .build();
        volunteerRepo.save(volunteer);

        return true;
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public boolean lockUser(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return false;
        user.setAccountLocked(true);
        user.setLockoutEnd(LocalDateTime.now().plusYears(100));
        userRepo.save(user);
        return true;
    }

    @Transactional
    public boolean unlockUser(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return false;
        user.setAccountLocked(false);
        user.setLockoutEnd(null);
        userRepo.save(user);
        return true;
    }
}
