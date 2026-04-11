package com.tinhnguyenxanh.controller;

import com.tinhnguyenxanh.entity.Donation;
import com.tinhnguyenxanh.repository.DonationRepository;
import com.tinhnguyenxanh.service.MomoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payments/momo")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final MomoService momoService;
    private final DonationRepository donationRepo;

    @PostMapping("/ipn")
    public ResponseEntity<String> handleIpn(@RequestParam String orderId,
                                           @RequestParam String resultCode,
                                           @RequestParam String transId,
                                           @RequestParam String extraData,
                                           @RequestParam String message,
                                           @RequestParam String signature) {
        log.info("MoMo IPN received: orderId={}, resultCode={}, transId={}, extraData={}, signature={}", 
                 orderId, resultCode, transId, extraData, signature.substring(0,16) + "...");
        
        try {
            boolean signatureValid = momoService.verifyIpnSignature(
                orderId, resultCode, transId, extraData, message, signature);
            if (!signatureValid) {
                log.warn("MoMo IPN signature invalid for orderId={}", orderId);
                return ResponseEntity.badRequest().body("Invalid signature");
            }

            Optional<Donation> donationOpt = donationRepo.findByTransactionCode(orderId);
            if (donationOpt.isEmpty()) {
                log.warn("Donation not found for orderId={}", orderId);
                return ResponseEntity.ok("OK");
            }

            Donation donation = donationOpt.get();
            if ("0".equals(resultCode)) {
                donation.setPaymentStatus("SUCCESS");
                donation.setPaid(true);
                donation.setProviderRef(transId);
            } else {
                donation.setPaymentStatus("FAILED");
                donation.setPaid(false);
            }
            donationRepo.save(donation);

            log.info("Donation {} updated via IPN: status={}, transId={}", 
                     orderId, donation.getPaymentStatus(), transId);

            // Decode extraData
            try {
                String decoded = new String(Base64.getDecoder().decode(extraData));
                log.info("IPN extraData decoded: {}", decoded);
            } catch (Exception e) {
                log.warn("Failed decode extraData: {}", e.getMessage());
            }

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("IPN error orderId={}: {}", orderId, e.getMessage());
            return ResponseEntity.status(500).body("Error");
        }
    }
}
