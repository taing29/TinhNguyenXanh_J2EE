package com.tinhnguyenxanh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ========== USER RELATIONSHIP ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ========== THÔNG TIN CƠ BẢN ==========
    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String organizationType;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Size(max = 500)
    private String avatarUrl;

    // ========== LĨNH VỰC HOẠT ĐỘNG ==========
    @NotBlank
    @Size(max = 500)
    @Column(nullable = false)
    private String focusAreas; // Lưu dạng comma-separated

    // ========== THÔNG TIN LIÊN HỆ ==========
    @NotBlank
    @Size(max = 200)
    @Email
    @Column(nullable = false)
    private String contactEmail;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false)
    private String phoneNumber;

    @Size(max = 300)
    private String website;

    // ========== ĐỊA CHỈ ==========
    @NotBlank
    @Size(max = 500)
    @Column(nullable = false)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String district;

    @Size(max = 100)
    private String ward;

    // ========== THÔNG TIN PHÁP LÝ ==========
    @Size(max = 20)
    private String taxCode;

    private LocalDate foundedDate;

    @Size(max = 200)
    private String legalRepresentative;

    // ========== XÁC MINH ==========
    @Size(max = 500)
    private String verificationDocsUrl;

    @Size(max = 100)
    private String documentType;

    private boolean verified = false;
    private LocalDateTime verifiedDate;

    @Size(max = 500)
    private String verificationNotes;

    // ========== MẠNG XÃ HỘI ==========
    @Size(max = 300)
    private String facebookUrl;

    @Size(max = 300)
    private String instagramUrl;

    @Size(max = 20)
    private String zaloNumber;

    // ========== THỐNG KÊ ==========
    private Integer memberCount;
    private Integer eventsOrganized;

    @Size(max = 1000)
    private String achievements;

    // ========== HỆ THỐNG ==========
    @Column(nullable = false)
    private LocalDateTime joinedDate = LocalDateTime.now();

    private LocalDateTime lastUpdated;
    private boolean isActive = true;
    private boolean isApproved = false;

    // ========== RATING ==========
    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating;

    private Integer totalReviews;

    // ========== RELATIONSHIPS ==========
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();
}
