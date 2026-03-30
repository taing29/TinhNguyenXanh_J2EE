package com.tinhnguyenxanh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_reports")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EventReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false)
    private String reportReason;

    @Column(nullable = false)
    private LocalDateTime reportDate = LocalDateTime.now();

    // Pending / Reviewed / Dismissed
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String status = "Pending";
}
