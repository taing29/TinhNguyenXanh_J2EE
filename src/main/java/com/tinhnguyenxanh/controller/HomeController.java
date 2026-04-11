package com.tinhnguyenxanh.controller;

import com.tinhnguyenxanh.dto.DonationDTO;
import com.tinhnguyenxanh.entity.Donation;
import com.tinhnguyenxanh.repository.DonationRepository;
import com.tinhnguyenxanh.repository.EventFavoriteRepository;
import com.tinhnguyenxanh.repository.OrganizationRepository;
import com.tinhnguyenxanh.security.CustomUserDetails;
import com.tinhnguyenxanh.service.EmailService;
import com.tinhnguyenxanh.service.EventService;
import com.tinhnguyenxanh.service.MomoService;
import com.tinhnguyenxanh.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EventService eventService;
    private final DonationRepository donationRepo;
    private final MomoService momoService;
    private final EmailService emailService;
    private final EventFavoriteRepository favoriteRepo;
    private final OrganizationRepository organizationRepo;
    private final SubscriptionService subscriptionService;

    @GetMapping({"/", "/home"})
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        try {
            var allApproved = eventService.getApprovedEvents();
            // Lấy tối đa 3 sự kiện nổi bật (nhiều đăng ký nhất) cho homepage
            var featuredEvents = allApproved.stream()
                    .sorted(java.util.Comparator.comparingInt(
                            com.tinhnguyenxanh.dto.EventDTO::getRegisteredCount).reversed())
                    .limit(3)
                    .toList();
            model.addAttribute("approvedEvents", featuredEvents);

            // Lấy 6 tổ chức đã được phê duyệt
            try {
                var approvedOrganizations = organizationRepo.findByIsApprovedTrue().stream()
                        .limit(6)
                        .toList();
                model.addAttribute("organizations", approvedOrganizations);
            } catch (Exception e) {
                System.err.println("[HOME] Error fetching organizations: " + e.getMessage());
                model.addAttribute("organizations", java.util.List.of());
            }

            // Truyền danh sách eventId đã yêu thích để frontend biết đánh dấu
            if (userDetails != null) {
                try {
                    Set<Integer> favIds = favoriteRepo.findByUser_Id(userDetails.getId())
                            .stream()
                            .map(ef -> ef.getEvent().getId())
                            .collect(Collectors.toSet());
                    model.addAttribute("favoritedIds", favIds);
                } catch (Exception e) {
                    System.err.println("[HOME] Error fetching favorites: " + e.getMessage());
                    model.addAttribute("favoritedIds", Set.of());
                }
            } else {
                model.addAttribute("favoritedIds", Set.of());
            }
        } catch (Exception e) {
            System.err.println("[HOME] Error on homepage: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("approvedEvents", java.util.List.of());
            model.addAttribute("organizations", java.util.List.of());
            model.addAttribute("favoritedIds", Set.of());
        }
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

    @PostMapping("/subscribe")
    public String subscribeNewsletter(@RequestParam String email, RedirectAttributes redirectAttrs) {
        SubscriptionService.SubscribeResult result = subscriptionService.subscribe(email);
        switch (result) {
            case CREATED -> redirectAttrs.addFlashAttribute("success", "Đăng ký nhận tin thành công!");
            case REACTIVATED -> redirectAttrs.addFlashAttribute("success", "Email đã được kích hoạt lại cho danh sách nhận tin.");
            case ALREADY_ACTIVE -> redirectAttrs.addFlashAttribute("success", "Email này đã có trong danh sách nhận tin.");
            default -> redirectAttrs.addFlashAttribute("error", "Vui lòng nhập địa chỉ email hợp lệ.");
        }
        return "redirect:/";
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