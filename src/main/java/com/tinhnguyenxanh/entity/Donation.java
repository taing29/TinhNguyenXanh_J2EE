package com.tinhnguyenxanh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String donorName;

    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal amount;

    @Column(nullable = false)
    private String phoneNumber;

    private String message;

    /** momo | bank */
    @Column(nullable = false)
    @Builder.Default
    private String paymentMethod = "momo";

    /** PENDING | SUCCESS | FAILED */
    @Column(nullable = false)
    @Builder.Default
    private String paymentStatus = "PENDING";

    /** Mã tham chiếu từ MoMo (transId) */
    private String providerRef;

    @Column(nullable = false)
    private boolean isPaid = false;

    /** Mã đơn gửi MoMo (orderId), ví dụ TNX123 */
    private String transactionCode;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
