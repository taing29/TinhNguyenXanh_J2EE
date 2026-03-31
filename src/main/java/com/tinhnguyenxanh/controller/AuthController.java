package com.tinhnguyenxanh.controller;

import com.tinhnguyenxanh.dto.RegisterDTO;
import com.tinhnguyenxanh.repository.VolunteerRepository;
import com.tinhnguyenxanh.repository.EventRegistrationRepository;
import com.tinhnguyenxanh.security.CustomUserDetails;
import com.tinhnguyenxanh.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final VolunteerRepository volunteerRepo;
    private final EventRegistrationRepository registrationRepo;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Email hoặc mật khẩu không đúng");
        if (logout != null) model.addAttribute("message", "Đăng xuất thành công");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDTO") RegisterDTO dto,
                           BindingResult result,
                           RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) return "auth/register";
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu xác nhận không khớp");
            return "auth/register";
        }
        boolean success = userService.register(dto);
        if (!success) {
            result.rejectValue("email", "error.email", "Email đã được sử dụng");
            return "auth/register";
        }
        redirectAttrs.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/auth/login";
    }

    // ===== VOLUNTEER PROFILE =====
    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        var volunteer = volunteerRepo.findByUser_Id(userDetails.getId()).orElse(null);
        var user = userDetails.getUser();
        // Stats
        int totalRegs = volunteer != null
                ? registrationRepo.countByVolunteer_Id(volunteer.getId())
                : 0;
        int confirmedRegs = volunteer != null
                ? registrationRepo.countByVolunteer_IdAndStatusIgnoreCase(volunteer.getId(), "Confirmed")
                : 0;
        model.addAttribute("user", user);
        model.addAttribute("volunteer", volunteer);
        model.addAttribute("totalRegs", totalRegs);
        model.addAttribute("confirmedRegs", confirmedRegs);
        return "auth/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        var volunteer = volunteerRepo.findByUser_Id(userDetails.getId()).orElse(null);
        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("volunteer", volunteer);
        return "auth/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam String fullName,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String address,
                                @RequestParam(required = false) String age,
                                @RequestParam(required = false) String bio,
                                @RequestParam(required = false) String skills,
                                @RequestParam(value = "avatarFile", required = false) org.springframework.web.multipart.MultipartFile avatarFile,
                                RedirectAttributes redirectAttrs) {
        if (userDetails == null) return "redirect:/auth/login";
        boolean ok = userService.updateVolunteerProfile(
                userDetails.getId(), fullName, phone, address, bio, skills, age, avatarFile);
        if (ok) redirectAttrs.addFlashAttribute("success", "Đã cập nhật thông tin cá nhân");
        else redirectAttrs.addFlashAttribute("error", "Cập nhật thất bại");
        return "redirect:/auth/profile";
    }
}
