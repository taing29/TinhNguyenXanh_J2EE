package com.tinhnguyenxanh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_registrations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private Volunteer volunteer;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String fullName;

    @NotBlank
    @Column(nullable = false)
    private String phone;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false)
    private String reason;

    // Pending / Confirmed / Rejected
    @Column(nullable = false)
    private String status = "Pending";

    @Column(nullable = false)
    private LocalDateTime registeredDate = LocalDateTime.now();
}
