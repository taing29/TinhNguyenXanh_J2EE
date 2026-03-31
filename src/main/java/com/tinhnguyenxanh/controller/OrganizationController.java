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

    @GetMapping
    public String listOrganizations(Model model) {
        model.addAttribute("organizations", orgService.getAllVerified());
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
        model.addAttribute("org", org);
        model.addAttribute("reviews", reviews);
        model.addAttribute("alreadyReviewed", alreadyReviewed);
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
