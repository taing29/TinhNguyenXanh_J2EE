package com.tinhnguyenxanh.controller;

import com.tinhnguyenxanh.dto.OrganizationDTO;
import com.tinhnguyenxanh.enums.Role;
import com.tinhnguyenxanh.security.CustomUserDetails;
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

    @GetMapping
    public String listOrganizations(Model model) {
        model.addAttribute("organizations", orgService.getAllVerified());
        return "organization/index";
    }

    @GetMapping("/{id}")
    public String orgDetail(@PathVariable Integer id, Model model) {
        var org = orgService.getById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tổ chức"));
        model.addAttribute("org", org);
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

    @GetMapping("/success")
    public String successPage() {
        return "organization/success";
    }
}
