package com.tinhnguyenxanh.controller.organizer;

import com.tinhnguyenxanh.dto.EventDTO;
import com.tinhnguyenxanh.dto.OrganizationDTO;
import com.tinhnguyenxanh.entity.Organization;
import com.tinhnguyenxanh.repository.EventCategoryRepository;
import com.tinhnguyenxanh.repository.VolunteerRepository;
import com.tinhnguyenxanh.security.CustomUserDetails;
import com.tinhnguyenxanh.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/organizer")
@PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
@RequiredArgsConstructor
public class OrganizerController {

    private final EventService eventService;
    private final EventRegistrationService registrationService;
    private final OrganizationService orgService;
    private final EventCategoryRepository categoryRepo;
    private final VolunteerRepository volunteerRepo;

    private Organization getOrg(CustomUserDetails userDetails) {
        return orgService.getByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tổ chức của bạn"));
    }

    // ===== DASHBOARD =====
    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Organization org = getOrg(userDetails);
        var events = eventService.getEventsByOrganization(org.getId());
        model.addAttribute("org", org);
        model.addAttribute("events", events);
        model.addAttribute("totalEvents", events.size());
        model.addAttribute("pendingEvents", events.stream()
                .filter(e -> "pending".equalsIgnoreCase(e.getStatus())).count());
        model.addAttribute("approvedEvents", events.stream()
                .filter(e -> "approved".equalsIgnoreCase(e.getStatus())).count());
        return "organizer/dashboard";
    }

    // ===== UPDATE ORG INFO =====
    @GetMapping("/profile")
    public String editOrgPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Organization org = getOrg(userDetails);
        OrganizationDTO dto = orgService.toPublicDTO(org);
        model.addAttribute("orgDTO", dto);
        return "organizer/org-profile";
    }

    @PostMapping("/profile")
    public String updateOrg(@Valid @ModelAttribute("orgDTO") OrganizationDTO dto,
                            BindingResult result,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model,
                            RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) return "organizer/org-profile";
        boolean ok = orgService.update(dto, userDetails.getId());
        if (ok) redirectAttrs.addFlashAttribute("success", "Đã cập nhật thông tin tổ chức");
        else redirectAttrs.addFlashAttribute("error", "Cập nhật thất bại");
        return "redirect:/organizer/dashboard";
    }

    // ===== EVENT MANAGEMENT =====
    @GetMapping("/events")
    public String myEvents(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam(required = false) String status,
                           @RequestParam(required = false) String hidden,
                           Model model) {
        Organization org = getOrg(userDetails);
        var events = eventService.getEventsByOrganization(org.getId());

        // Filter by status
        if (status != null && !status.isBlank()) {
            events = events.stream()
                    .filter(e -> status.equalsIgnoreCase(e.getStatus()))
                    .toList();
        }
        // Filter by hidden
        if ("true".equals(hidden)) {
            events = events.stream().filter(EventDTO::isHidden).toList();
        } else if ("false".equals(hidden)) {
            events = events.stream().filter(e -> !e.isHidden()).toList();
        }

        model.addAttribute("events", events);
        model.addAttribute("filterStatus", status);
        model.addAttribute("filterHidden", hidden);
        return "organizer/events";
    }

    @GetMapping("/events/create")
    public String createEventPage(Model model) {
        model.addAttribute("eventDTO", new EventDTO());
        model.addAttribute("categories", categoryRepo.findAll());
        return "organizer/event-create";
    }

    @PostMapping("/events/create")
    public String createEvent(@Valid @ModelAttribute("eventDTO") EventDTO dto,
                              BindingResult result,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model,
                              RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepo.findAll());
            return "organizer/event-create";
        }
        Organization org = getOrg(userDetails);
        boolean ok = eventService.createEvent(dto, org.getId());
        if (ok) redirectAttrs.addFlashAttribute("success", "Đã tạo sự kiện! Chờ admin duyệt.");
        else redirectAttrs.addFlashAttribute("error", "Tạo sự kiện thất bại");
        return "redirect:/organizer/events";
    }

    @GetMapping("/events/{id}/edit")
    public String editEventPage(@PathVariable Integer id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        Organization org = getOrg(userDetails);
        var eventDTO = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));
        if (!org.getId().equals(eventDTO.getOrganizationId()))
            return "redirect:/organizer/events";
        model.addAttribute("eventDTO", eventDTO);
        model.addAttribute("categories", categoryRepo.findAll());
        return "organizer/event-edit";
    }

    @PostMapping("/events/{id}/edit")
    public String editEvent(@PathVariable Integer id,
                            @Valid @ModelAttribute("eventDTO") EventDTO dto,
                            BindingResult result,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model,
                            RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepo.findAll());
            return "organizer/event-edit";
        }
        Organization org = getOrg(userDetails);
        dto.setId(id);
        boolean ok = eventService.updateEvent(dto, org.getId());
        if (ok) redirectAttrs.addFlashAttribute("success", "Đã cập nhật sự kiện");
        else redirectAttrs.addFlashAttribute("error", "Cập nhật thất bại");
        return "redirect:/organizer/events";
    }

    @PostMapping("/events/{id}/toggle-hidden")
    public String toggleHidden(@PathVariable Integer id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttrs) {
        Organization org = getOrg(userDetails);
        boolean ok = eventService.toggleHidden(id, org.getId());
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã cập nhật trạng thái ẩn/hiện sự kiện" : "Không thể thực hiện");
        return "redirect:/organizer/events";
    }

    // ===== VOLUNTEER MANAGEMENT =====
    @GetMapping("/events/{id}/volunteers")
    public String eventVolunteers(@PathVariable Integer id,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  Model model) {
        Organization org = getOrg(userDetails);
        var eventDTO = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));
        if (!org.getId().equals(eventDTO.getOrganizationId()))
            return "redirect:/organizer/events";
        model.addAttribute("event", eventDTO);
        model.addAttribute("registrations", registrationService.getByEventId(id));
        return "organizer/volunteers";
    }

    @GetMapping("/volunteers/{volunteerId}/profile")
    public String volunteerProfile(@PathVariable Integer volunteerId, Model model) {
        var volunteer = volunteerRepo.findById(volunteerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tình nguyện viên"));
        model.addAttribute("volunteer", volunteer);
        return "organizer/volunteer-profile";
    }

    @PostMapping("/registrations/{id}/approve")
    public String approveReg(@PathVariable Integer id,
                             @RequestParam(required = false, defaultValue = "") String returnUrl,
                             RedirectAttributes redirectAttrs) {
        boolean ok = registrationService.approve(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã xác nhận tình nguyện viên" : "Không thể xác nhận");
        return returnUrl.isBlank() ? "redirect:/organizer/events" : "redirect:" + returnUrl;
    }

    @PostMapping("/registrations/{id}/reject")
    public String rejectReg(@PathVariable Integer id,
                            @RequestParam(required = false, defaultValue = "") String returnUrl,
                            RedirectAttributes redirectAttrs) {
        boolean ok = registrationService.reject(id);
        redirectAttrs.addFlashAttribute(ok ? "success" : "error",
                ok ? "Đã từ chối tình nguyện viên" : "Không thể từ chối");
        return returnUrl.isBlank() ? "redirect:/organizer/events" : "redirect:" + returnUrl;
    }
}
