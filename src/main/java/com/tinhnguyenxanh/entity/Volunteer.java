package com.tinhnguyenxanh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "volunteers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String fullName;

    @Email
    private String email;

    private String phone;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String avatarUrl;

    @Column(nullable = false)
    private LocalDateTime joinedDate = LocalDateTime.now();

    // Available / Busy / Inactive
    @Column(nullable = false)
    private String availability = "Available";
}
