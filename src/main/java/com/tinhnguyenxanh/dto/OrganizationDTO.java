package com.tinhnguyenxanh.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrganizationDTO {
    private Integer id;
    private Long userId;

    @NotBlank(message = "Tên tổ chức không được để trống")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Loại tổ chức không được để trống")
    private String organizationType;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 2000)
    private String description;

    private String avatarUrl;
    private transient MultipartFile avatarFile;

    @NotEmpty(message = "Vui lòng chọn ít nhất một lĩnh vực")
    private List<String> focusAreas;

    @NotBlank @Email
    private String contactEmail;

    @NotBlank
    private String phoneNumber;

    private String website;
    private String address;
    private String city;
    private String district;
    private String ward;
    private String taxCode;
    private LocalDate foundedDate;
    private String legalRepresentative;
    private String verificationDocsUrl;
    private String documentType;
    private String facebookUrl;
    private String instagramUrl;
    private String zaloNumber;
    private Integer memberCount;
    private Integer eventsOrganized;
    private String achievements;
    private boolean verified;
    private boolean isApproved;
    private LocalDateTime joinedDate;
    private BigDecimal averageRating;
    private Integer totalReviews;
}
