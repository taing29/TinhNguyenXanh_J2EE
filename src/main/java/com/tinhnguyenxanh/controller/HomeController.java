package com.tinhnguyenxanh.controller;

import com.tinhnguyenxanh.dto.DonationDTO;
import com.tinhnguyenxanh.entity.Donation;
import com.tinhnguyenxanh.repository.DonationRepository;
import com.tinhnguyenxanh.service.EmailService;
import com.tinhnguyenxanh.service.EventService;
import com.tinhnguyenxanh.service.MomoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EventService eventService;
    private final DonationRepository donationRepo;
    private final MomoService momoService;
    private final EmailService emailService;

    @GetMapping({"/", "/home"})
    public String index(Model model) {
        var allApproved = eventService.getApprovedEvents();
        var top3 = allApproved.stream()
                .sorted(java.util.Comparator.comparingInt(
                        com.tinhnguyenxanh.dto.EventDTO::getRegisteredCount).reversed())
                .limit(3)
                .toList();
        model.addAttribute("approvedEvents", top3);
        return "home/index";
    }

    @GetMapping("/about")
    public String about() {
        return "home/about";
    }

    @GetMapping("/contact")
    public String contactPage(Model model) {
        return "home/contact";
    }

    @PostMapping("/contact")
    public String sendContact(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String message,
                              RedirectAttributes redirectAttrs) {
        try {
            String body = String.format(
                "<h3>Tin nhắn liên hệ từ website</h3>" +
                "<p><b>Tên:</b> %s</p><p><b>Email:</b> %s</p><p><b>Nội dung:</b><br>%s</p>",
                name, email, message);
            emailService.sendEmail("admin@tinhnguyenxanh.vn", "Liên hệ từ: " + name, body, email, name);
            redirectAttrs.addFlashAttribute("success", "Tin nhắn đã được gửi!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Không thể gửi tin nhắn. Vui lòng thử lại.");
        }
        return "redirect:/contact";
    }

    @GetMapping("/donate")
    public String donatePage(Model model) {
        model.addAttribute("donationDTO", new DonationDTO());
        return "home/donate";
    }

    @PostMapping("/donate")
    public String processDonate(@Valid @ModelAttribute DonationDTO dto,
                                BindingResult result,
                                RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) return "home/donate";

        Donation donation = Donation.builder()
                .donorName(dto.getDonorName())
                .amount(dto.getAmount())
                .phoneNumber(dto.getPhoneNumber())
                .message(dto.getMessage())
                .isPaid(false)
                .createdAt(LocalDateTime.now())
                .build();
        donationRepo.save(donation);

        String orderId = "TNX" + donation.getId();
        String payUrl = momoService.createPayment(
                "Quyên góp - " + dto.getDonorName(),
                orderId,
                dto.getAmount().toBigInteger().toString());

        if (payUrl.startsWith("http")) {
            return "redirect:" + payUrl;
        }

        redirectAttrs.addFlashAttribute("error", "Không thể kết nối MoMo. Vui lòng thử lại.");
        return "redirect:/donate";
    }

    @GetMapping("/payment/momo/result")
    public String momoResult(@RequestParam(required = false) String orderId,
                             @RequestParam(required = false) String resultCode,
                             Model model) {
        boolean success = "0".equals(resultCode);
        if (success && orderId != null && orderId.startsWith("TNX")) {
            try {
                Integer donationId = Integer.parseInt(orderId.replace("TNX", ""));
                donationRepo.findById(donationId).ifPresent(d -> {
                    d.setPaid(true);
                    d.setTransactionCode(orderId);
                    donationRepo.save(d);
                });
            } catch (Exception ignored) {}
        }
        model.addAttribute("success", success);
        model.addAttribute("orderId", orderId);
        return "home/payment-result";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.toLowerCase();
            var events = eventService.getApprovedEvents().stream()
                    .filter(e -> e.getTitle().toLowerCase().contains(kw)
                            || (e.getDescription() != null && e.getDescription().toLowerCase().contains(kw))
                            || (e.getLocation() != null && e.getLocation().toLowerCase().contains(kw)))
                    .toList();
            model.addAttribute("events", events);
            model.addAttribute("keyword", keyword);
        }
        return "home/search";
    }
}
