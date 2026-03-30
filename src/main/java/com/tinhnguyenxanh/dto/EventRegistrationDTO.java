package com.tinhnguyenxanh.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventRegistrationDTO {
    private Integer eventId;

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotBlank
    private String phone;

    @NotBlank
    @Size(max = 500)
    private String reason;
}
