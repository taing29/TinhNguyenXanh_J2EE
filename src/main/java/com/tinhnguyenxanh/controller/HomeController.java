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
import org.springframework.web.util.HtmlUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.time.LocalDateTime;
import java.util.Optional;
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
                              @RequestParam String subject,
                              @RequestParam String message,
                              RedirectAttributes redirectAttrs) {
        String safeName = name != null ? name.trim() : "";
        String safeEmail = email != null ? email.trim() : "";
        String safeSubject = subject != null ? subject.trim() : "";
        String safeMessage = message != null ? message.trim() : "";
        if (safeName.isEmpty() || safeEmail.isEmpty() || safeSubject.isEmpty() || safeMessage.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Vui lòng điền đầy đủ thông tin.");
            return "redirect:/contact";
        }
        try {
            String lineSubject = safeSubject.length() > 120 ? safeSubject.substring(0, 120) + "…" : safeSubject;
            String body = String.format(
                    "<h3>Tin nhắn liên hệ từ website</h3>" +
                            "<p><b>Tên:</b> %s</p><p><b>Email:</b> %s</p><p><b>Chủ đề:</b> %s</p><p><b>Nội dung:</b><br>%s</p>",
                    HtmlUtils.htmlEscape(safeName),
                    HtmlUtils.htmlEscape(safeEmail),
                    HtmlUtils.htmlEscape(safeSubject),
                    HtmlUtils.htmlEscape(safeMessage).replace("\n", "<br/>"));
            emailService.sendEmail(
                    "admin@tinhnguyenxanh.vn",
                    "[Liên hệ web] " + lineSubject,
                    body,
                    safeEmail,
                    safeName);
            redirectAttrs.addFlashAttribute("success",
                    "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất có thể qua email.");
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

        String method = dto.getPaymentMethod() != null ? dto.getPaymentMethod().trim().toLowerCase() : "momo";
        if (!"momo".equals(method) && !"bank".equals(method)) {
            method = "momo";
        }

        String phone = dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()
                ? dto.getPhoneNumber().trim() : "—";

        Donation donation = Donation.builder()
                .donorName(dto.getDonorName().trim())
                .amount(dto.getAmount())
                .phoneNumber(phone)
                .message(dto.getMessage() != null && !dto.getMessage().isBlank() ? dto.getMessage().trim() : null)
                .paymentMethod(method)
                .paymentStatus("PENDING")
                .isPaid(false)
                .createdAt(LocalDateTime.now())
                .build();
        donationRepo.save(donation);

        String orderId = "TNX" + donation.getId();
        donation.setTransactionCode(orderId);
        donationRepo.save(donation);

        if ("bank".equals(method)) {
            return "redirect:/payment/momo/result?txn=" + URLEncoder.encode(orderId, StandardCharsets.UTF_8)
                    + "&amount=" + dto.getAmount().toPlainString()
                    + "&method=bank";
        }

        String orderInfo = (dto.getMessage() != null && !dto.getMessage().isBlank())
                ? dto.getMessage().trim()
                : "Ủng hộ Tình Nguyện Xanh — " + dto.getDonorName().trim();
        String rawExtraData = String.format("{\"donationId\":%d,\"orderId\":\"%s\"}", donation.getId(), orderId);
        String extraData = Base64.getEncoder().encodeToString(rawExtraData.getBytes(StandardCharsets.UTF_8));

        String payUrl = momoService.createPayment(
                orderInfo,
                orderId,
                dto.getAmount().toBigInteger().toString(),
                extraData);

        if (payUrl.startsWith("http")) {
            return "redirect:" + payUrl;
        }

        redirectAttrs.addFlashAttribute("error",
                (payUrl != null && (payUrl.startsWith("LỖI") || payUrl.startsWith("Lỗi")))
                        ? payUrl
                        : "Không thể kết nối MoMo. Vui lòng thử lại.");
        return "redirect:/donate";
    }

    @GetMapping("/payment/momo/result")
    public String momoResult(@RequestParam(required = false) String orderId,
                             @RequestParam(required = false) String txn,
                             @RequestParam(required = false) String resultCode,
                             @RequestParam(required = false) String amount,
                             @RequestParam(required = false) String method,
                             Model model) {
        String code = (txn != null && !txn.isBlank()) ? txn : orderId;

        Optional<Donation> donationOpt = (code != null && !code.isBlank())
                ? donationRepo.findByTransactionCode(code)
                : Optional.empty();

        boolean rcOk = "0".equals(resultCode);
        boolean rcFail = resultCode != null && !resultCode.isBlank() && !"0".equals(resultCode);

        donationOpt.ifPresent(d -> {
            if (!"momo".equalsIgnoreCase(d.getPaymentMethod())) {
                return;
            }
            if (!"PENDING".equals(d.getPaymentStatus())) {
                return;
            }
            if (rcOk) {
                d.setPaymentStatus("SUCCESS");
                d.setPaid(true);
                donationRepo.save(d);
            } else if (rcFail) {
                d.setPaymentStatus("FAILED");
                d.setPaid(false);
                donationRepo.save(d);
            }
        });

        Donation donation = donationOpt.orElse(null);
        String display;
        if (donation != null) {
            if ("bank".equalsIgnoreCase(donation.getPaymentMethod())) {
                display = "pending";
            } else if ("SUCCESS".equals(donation.getPaymentStatus()) || donation.isPaid()) {
                display = "success";
            } else if ("FAILED".equals(donation.getPaymentStatus())) {
                display = "failed";
            } else {
                display = "pending";
            }
            model.addAttribute("donation", donation);
        } else {
            display = "pending";
        }

        model.addAttribute("displayStatus", display);
        model.addAttribute("txn", code);
        model.addAttribute("queryAmount", amount);
        model.addAttribute("queryMethod", method != null ? method : "momo");
        model.addAttribute("resultCode", resultCode);
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