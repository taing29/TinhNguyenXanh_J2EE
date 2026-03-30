package com.tinhnguyenxanh.controller.admin;

import com.tinhnguyenxanh.entity.*;
import com.tinhnguyenxanh.repository.*;
import com.tinhnguyenxanh.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;
    private final OrganizationService orgService;
    private final UserService userService;
    private final EventCategoryRepository categoryRepo;
    private final EventReportRepository reportRepo;
    private final DonationRepository donationRepo;

    // ===== DASHBOARD =====
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalEvents", eventService.getAllEvents().size());
        model.addAttribute("pendingEvents", eventService.getAllEvents().stream()
                .filter(e -> "pending".equalsIgnoreCase(e.getStatus())).count());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("pendingOrgs", orgService.getPendingApproval().size());
        model.addAttribute("totalDonations", donationRepo.count());
        return "admin/dashboard";
    }

    // ===== EVENTS =====
    @GetMapping("/events")
    public String adminEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "admin/events";
    }

    @PostMapping("/events/{id}/approve")
    public String approveEvent(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        boolean ok = eventService.approveEvent(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã duyệt sự kiện" : "Không thể duyệt sự kiện");
        return "redirect:/admin/events";
    }

    @PostMapping("/events/{id}/reject")
    public String rejectEvent(@PathVariable Integer id,
                              @RequestParam(defaultValue = "Vi phạm quy định") String reason,
                              RedirectAttributes redirectAttrs) {
        boolean ok = eventService.rejectEvent(id, reason);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã từ chối sự kiện" : "Không thể từ chối");
        return "redirect:/admin/events";
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        boolean ok = eventService.deleteEvent(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã xóa sự kiện" : "Không thể xóa");
        return "redirect:/admin/events";
    }

    // ===== ORGANIZATIONS =====
    @GetMapping("/organizations")
    public String adminOrganizations(Model model) {
        model.addAttribute("pendingOrgs", orgService.getPendingApproval());
        return "admin/organizations";
    }

    @PostMapping("/organizations/{id}/approve")
    public String approveOrg(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        boolean ok = orgService.approve(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã duyệt tổ chức" : "Không thể duyệt");
        return "redirect:/admin/organizations";
    }

    @PostMapping("/organizations/{id}/reject")
    public String rejectOrg(@PathVariable Integer id,
                            @RequestParam(defaultValue = "") String notes,
                            RedirectAttributes redirectAttrs) {
        boolean ok = orgService.reject(id, notes);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã từ chối tổ chức" : "Không thể từ chối");
        return "redirect:/admin/organizations";
    }

    // ===== USERS =====
    @GetMapping("/users")
    public String adminUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/lock")
    public String lockUser(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        boolean ok = userService.lockUser(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã khóa tài khoản" : "Không thể khóa");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unlock")
    public String unlockUser(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        boolean ok = userService.unlockUser(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã mở khóa tài khoản" : "Không thể mở khóa");
        return "redirect:/admin/users";
    }

    // ===== CATEGORIES =====
    @GetMapping("/categories")
    public String adminCategories(Model model) {
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("newCategory", new EventCategory());
        return "admin/categories";
    }

    @PostMapping("/categories/create")
    public String createCategory(@RequestParam String name, RedirectAttributes redirectAttrs) {
        if (name == null || name.isBlank()) {
            redirectAttrs.addFlashAttribute("error", "Tên danh mục không được để trống");
            return "redirect:/admin/categories";
        }
        EventCategory cat = new EventCategory();
        cat.setName(name.trim());
        categoryRepo.save(cat);
        redirectAttrs.addFlashAttribute("success", "Đã thêm danh mục");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        categoryRepo.deleteById(id);
        redirectAttrs.addFlashAttribute("success", "Đã xóa danh mục");
        return "redirect:/admin/categories";
    }

    // ===== REPORTS =====
    @GetMapping("/reports")
    public String adminReports(Model model) {
        model.addAttribute("reports", reportRepo.findPendingWithDetails());
        return "admin/reports";
    }

    @PostMapping("/reports/{id}/dismiss")
    public String dismissReport(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        reportRepo.findById(id).ifPresent(r -> {
            r.setStatus("Dismissed");
            reportRepo.save(r);
        });
        redirectAttrs.addFlashAttribute("success", "Đã bỏ qua báo cáo");
        return "redirect:/admin/reports";
    }
}
