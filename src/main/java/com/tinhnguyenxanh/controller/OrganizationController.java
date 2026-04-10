package com.tinhnguyenxanh.controller;

import com.tinhnguyenxanh.dto.OrganizationDTO;
import com.tinhnguyenxanh.enums.Role;
import com.tinhnguyenxanh.security.CustomUserDetails;
import com.tinhnguyenxanh.entity.Review;
import com.tinhnguyenxanh.entity.User;
import com.tinhnguyenxanh.repository.ReviewRepository;
import com.tinhnguyenxanh.repository.UserRepository;
import com.tinhnguyenxanh.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService orgService;
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final com.tinhnguyenxanh.service.EventService eventService;

    @GetMapping
    public String listOrganizations(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(required = false) String q,
                                    Model model) {
        int pageSize = 6;
        var all = orgService.getAllVerified();

        // Lọc theo từ khoá tìm kiếm
        if (q != null && !q.isBlank()) {
            String kw = q.toLowerCase().trim();
            all = all.stream()
                    .filter(o -> o.getName() != null && o.getName().toLowerCase().contains(kw)
                            || o.getDescription() != null && o.getDescription().toLowerCase().contains(kw)
                            || o.getOrganizationType() != null && o.getOrganizationType().toLowerCase().contains(kw))
                    .toList();
            model.addAttribute("searchQuery", q);
        }

        int total = all.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        page = Math.max(0, Math.min(page, totalPages - 1));
        var paged = all.stream().skip((long) page * pageSize).limit(pageSize).toList();
        model.addAttribute("organizations", paged);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalOrgs", total);
        return "organization/index";
    }

    @GetMapping("/{id}")
    public String orgDetail(@PathVariable Integer id,
                            @AuthenticationPrincipal com.tinhnguyenxanh.security.CustomUserDetails userDetails,
                            Model model) {
        var org = orgService.getById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tổ chức"));
        var reviews = reviewRepo.findByOrganization_Id(id);
        boolean alreadyReviewed = userDetails != null && reviewRepo.existsByUser_IdAndOrganization_Id(userDetails.getId(), id);

        // Các sự kiện đã approved của tổ chức, tính sẵn isExpired ở Java
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        var orgEvents = eventService.getEventsByOrganization(id).stream()
                .filter(e -> "approved".equalsIgnoreCase(e.getStatus()) && !e.isHidden())
                .sorted(java.util.Comparator.comparing(
                        com.tinhnguyenxanh.dto.EventDTO::getStartTime).reversed())
                .collect(java.util.stream.Collectors.toList());

        // Tập hợp các id event đã kết thúc để template dùng
        java.util.Set<Integer> expiredIds = orgEvents.stream()
                .filter(e -> e.getEndTime() != null && e.getEndTime().isBefore(now))
                .map(com.tinhnguyenxanh.dto.EventDTO::getId)
                .collect(java.util.stream.Collectors.toSet());

        model.addAttribute("org", org);
        model.addAttribute("reviews", reviews);
        model.addAttribute("alreadyReviewed", alreadyReviewed);
        model.addAttribute("orgEvents", orgEvents);
        model.addAttribute("expiredIds", expiredIds);
        return "organization/details";
    }

    @GetMapping("/register")
    public String registerPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        if (userDetails.getUser().getRole() != Role.VOLUNTEER) {
            model.addAttribute("error", "Chỉ tài khoản tình nguyện viên mới có thể đăng ký tổ chức.");
            return "organization/success";
        }
        if (orgService.getByUserId(userDetails.getId()).isPresent()) {
            model.addAttribute("error", "Bạn đã gửi đăng ký tổ chức trước đó. Vui lòng chờ admin duyệt.");
            return "organization/success";
        }
        model.addAttribute("orgDTO", new OrganizationDTO());
        return "organization/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("orgDTO") OrganizationDTO dto,
                           BindingResult result,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           RedirectAttributes redirectAttrs) {
        if (userDetails == null) return "redirect:/auth/login";
        if (userDetails.getUser().getRole() != Role.VOLUNTEER) {
            redirectAttrs.addFlashAttribute("error", "Chỉ tài khoản tình nguyện viên mới có thể đăng ký tổ chức.");
            return "redirect:/organizations/success";
        }
        if (result.hasErrors()) return "organization/register";

        boolean success = orgService.register(dto, userDetails.getId());
        if (success) {
            redirectAttrs.addFlashAttribute("success", "Đăng ký tổ chức thành công! Chờ xét duyệt.");
        } else {
            redirectAttrs.addFlashAttribute("error", "Đăng ký thất bại. Vui lòng thử lại.");
        }
        return "redirect:/organizations/success";
    }

    @PostMapping("/{id}/review")
    public String submitReview(@PathVariable Integer id,
                               @RequestParam int rating,
                               @RequestParam(required = false) String comment,
                               @org.springframework.security.core.annotation.AuthenticationPrincipal com.tinhnguyenxanh.security.CustomUserDetails userDetails,
                               org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (userDetails == null) return "redirect:/auth/login";
        if (reviewRepo.existsByUser_IdAndOrganization_Id(userDetails.getId(), id)) {
            redirectAttrs.addFlashAttribute("error", "Bạn đã đánh giá tổ chức này rồi");
            return "redirect:/organizations/" + id;
        }
        try {
            var org = orgService.getEntityById(id).orElseThrow();
            User user = userRepo.findById(userDetails.getId()).orElseThrow();
            Review review = Review.builder()
                    .organization(org)
                    .user(user)
                    .rating(Math.max(1, Math.min(5, rating)))
                    .comment(comment != null ? comment.trim() : "")
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            reviewRepo.save(review);
            redirectAttrs.addFlashAttribute("success", "Đã gửi đánh giá!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Không thể gửi đánh giá");
        }
        return "redirect:/organizations/" + id;
    }

    @GetMapping("/success")
    public String successPage() {
        return "organization/success";
    }
}