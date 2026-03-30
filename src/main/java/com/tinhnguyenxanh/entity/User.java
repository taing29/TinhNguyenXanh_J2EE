package com.tinhnguyenxanh.entity;

import com.tinhnguyenxanh.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    private String phoneNumber;
    private String address;
    private String age;
    private String avatarPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.VOLUNTEER;

    @Column(nullable = false)
    private LocalDateTime registeredDate = LocalDateTime.now();

    private boolean enabled = true;
    private boolean accountLocked = false;
    private LocalDateTime lockoutEnd;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EventFavorite> favoriteEvents;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EventReport> submittedReports;
}
