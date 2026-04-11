package com.tinhnguyenxanh.controller.admin;

import com.tinhnguyenxanh.entity.*;
import com.tinhnguyenxanh.enums.Role;
import com.tinhnguyenxanh.repository.*;
import com.tinhnguyenxanh.service.*;
import com.tinhnguyenxanh.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
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
    private final SubscriptionService subscriptionService;
    private final EventRegistrationService registrationService;

    // ===== DASHBOARD =====
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalEvents", eventService.getAllEvents().size());
        model.addAttribute("pendingEvents", eventService.getAllEvents().stream()
                .filter(e -> "pending".equalsIgnoreCase(e.getStatus())).count());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("pendingOrgs", orgService.getPendingApproval().size());
        model.addAttribute("totalDonations", donationRepo.count());
        model.addAttribute("totalSubscriptions", subscriptionService.getAllSubscriptions().size());
        model.addAttribute("activeSubscriptions", subscriptionService.countActiveSubscriptions());
        model.addAttribute("totalRegistrations", registrationService.getAllRegistrations().size());
        model.addAttribute("pendingRegistrations", registrationService.countPending());
        return "admin/dashboard";
    }

    // ===== SUBSCRIPTIONS =====
    @GetMapping("/subscriptions")
    public String adminSubscriptions(Model model) {
        List<NewsletterSubscription> subscriptions = subscriptionService.getAllSubscriptions();
        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("totalSubscriptions", subscriptions.size());
        model.addAttribute("activeSubscriptions", subscriptions.stream().filter(NewsletterSubscription::isActive).count());
        return "admin/subscriptions";
    }

    @PostMapping("/subscriptions/{id}/deactivate")
    public String deactivateSubscription(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        boolean ok = subscriptionService.deactivate(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã tắt đăng ký nhận tin" : "Không thể cập nhật subscription");
        return "redirect:/admin/subscriptions";
    }

    @PostMapping("/subscriptions/{id}/reactivate")
    public String reactivateSubscription(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        boolean ok = subscriptionService.reactivate(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã kích hoạt lại đăng ký nhận tin" : "Không thể cập nhật subscription");
        return "redirect:/admin/subscriptions";
    }

    @PostMapping("/subscriptions/{id}/delete")
    public String deleteSubscription(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        boolean ok = subscriptionService.deleteSubscription(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã xóa subscription" : "Không thể xóa subscription");
        return "redirect:/admin/subscriptions";
    }

    // ===== DONATIONS =====
    @GetMapping("/donations")
    public String adminDonations(Model model) {
        List<Donation> donations = donationRepo.findAll().stream()
                .sorted((left, right) -> right.getCreatedAt().compareTo(left.getCreatedAt()))
                .toList();
        BigDecimal totalAmount = donations.stream()
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long paidCount = donations.stream().filter(Donation::isPaid).count();

        model.addAttribute("donations", donations);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("paidCount", paidCount);
        model.addAttribute("pendingCount", donations.size() - paidCount);
        return "admin/donations";
    }

    // ===== EVENT REGISTRATIONS =====
    @GetMapping("/registrations")
    public String adminRegistrations(Model model) {
        List<EventRegistration> registrations = registrationService.getAllRegistrations();
        model.addAttribute("registrations", registrations);
        model.addAttribute("totalRegistrations", registrations.size());
        model.addAttribute("pendingRegistrations", registrationService.countPending());
        model.addAttribute("confirmedRegistrations", registrationService.countConfirmedAll());
        model.addAttribute("rejectedRegistrations", registrationService.countRejectedAll());
        return "admin/registrations";
    }

    @PostMapping("/registrations/{id}/approve")
    public String approveRegistration(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        boolean ok = registrationService.approve(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã xác nhận đăng ký" : "Không thể xác nhận đăng ký");
        return "redirect:/admin/registrations";
    }

    @PostMapping("/registrations/{id}/reject")
    public String rejectRegistration(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        boolean ok = registrationService.reject(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã từ chối đăng ký" : "Không thể từ chối đăng ký");
        return "redirect:/admin/registrations";
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
    public String adminUsers(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("currentUserId", currentUser != null ? currentUser.getId() : null);
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id,
                                 @RequestParam Role role,
                                 @AuthenticationPrincipal CustomUserDetails currentUser,
                                 RedirectAttributes redirectAttrs) {
        if (currentUser != null && currentUser.getId().equals(id)) {
            redirectAttrs.addFlashAttribute("error", "Bạn không thể tự thay đổi vai trò của chính mình");
            return "redirect:/admin/users";
        }
        boolean ok = userService.updateUserRole(id, role);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã cập nhật vai trò người dùng" : "Không thể cập nhật vai trò");
        return "redirect:/admin/users";
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
    public String adminReports(@RequestParam(defaultValue = "all") String status, Model model) {
        List<EventReport> reports = reportRepo.findAllWithDetails();
        List<EventReport> filteredReports = reports.stream()
                .filter(r -> "all".equalsIgnoreCase(status) || r.getStatus().equalsIgnoreCase(status))
                .toList();

        model.addAttribute("reports", filteredReports);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("totalReports", reports.size());
        model.addAttribute("pendingReports", reports.stream().filter(r -> "Pending".equalsIgnoreCase(r.getStatus())).count());
        model.addAttribute("approvedReports", reports.stream().filter(r -> "Approved".equalsIgnoreCase(r.getStatus())).count());
        model.addAttribute("rejectedReports", reports.stream().filter(r -> "Rejected".equalsIgnoreCase(r.getStatus())).count());
        return "admin/reports";
    }

    @PostMapping("/reports/{id}/dismiss")
    public String dismissReport(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        boolean ok = eventService.rejectReport(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã từ chối báo cáo" : "Không thể từ chối báo cáo");
        return "redirect:/admin/reports";
    }

    @PostMapping("/reports/{id}/approve")
    public String approveReport(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        boolean ok = eventService.approveReport(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã phê duyệt báo cáo" : "Không thể phê duyệt báo cáo");
        return "redirect:/admin/reports";
    }

    @PostMapping("/reports/{id}/reject")
    public String rejectReport(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
        boolean ok = eventService.rejectReport(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã từ chối báo cáo" : "Không thể từ chối báo cáo");
        return "redirect:/admin/reports";
    }
}
