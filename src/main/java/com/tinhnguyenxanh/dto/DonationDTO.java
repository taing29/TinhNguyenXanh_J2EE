package com.tinhnguyenxanh.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DonationDTO {

    @NotBlank(message = "Tên người quyên góp không được để trống")
    private String donorName;

    @NotNull(message = "Số tiền không được để trống")
    @Min(value = 10000, message = "Số tiền tối thiểu là 10,000 VNĐ")
    private BigDecimal amount;

    /** Để trống nếu không muốn cung cấp (một số kênh không bắt buộc). */
    private String phoneNumber;

    private String message;

    /** momo | bank */
    private String paymentMethod = "momo";
}
