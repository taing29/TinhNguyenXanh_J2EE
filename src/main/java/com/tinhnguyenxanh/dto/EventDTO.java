package com.tinhnguyenxanh.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EventDTO {
    private Integer id;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 150)
    private String title;

    private String description;
    private String status;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    private LocalDateTime endTime;

    private String location;
    private String locationCoords;

    private Integer organizationId;
    private String organizationName;
    private Integer categoryId;
    private String categoryName;

    @Min(1)
    private int maxVolunteers;

    private int registeredCount;
    private String images;
    private boolean isHidden;
    private String hiddenReason;

    // Dùng khi upload ảnh
    private transient MultipartFile imageFile;
}
