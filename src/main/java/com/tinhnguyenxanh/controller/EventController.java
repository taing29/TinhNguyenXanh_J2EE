package com.tinhnguyenxanh.controller;

import com.tinhnguyenxanh.dto.EventRegistrationDTO;
import com.tinhnguyenxanh.entity.*;
import com.tinhnguyenxanh.repository.*;
import com.tinhnguyenxanh.security.CustomUserDetails;
import com.tinhnguyenxanh.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventRegistrationService registrationService;
    private final EventCategoryRepository categoryRepo;
    private final EventFavoriteRepository favoriteRepo;
    private final EventCommentRepository commentRepo;
    private final EventReportRepository reportRepo;
    private final VolunteerRepository volunteerRepo;
    private final UserRepository userRepo;
    private final EventRepository eventRepo;

    @GetMapping
    public String listEvents(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(required = false) String category,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        int pageSize = 6;
        var all = eventService.getApprovedEvents();
        // Filter by category if provided
        if (category != null && !category.isBlank()) {
            all = all.stream()
                    .filter(e -> category.equals(String.valueOf(e.getCategoryId())))
                    .toList();
            model.addAttribute("selectedCategory", category);
        }
        int total = all.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        page = Math.max(0, Math.min(page, totalPages - 1));
        var paged = all.stream().skip((long) page * pageSize).limit(pageSize).toList();
        model.addAttribute("events", paged);
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalEvents", total);

        // Truyền danh sách eventId đã yêu thích để hiển thị đúng icon
        if (userDetails != null) {
            Set<Integer> favIds = favoriteRepo.findByUser_Id(userDetails.getId())
                    .stream()
                    .map(ef -> ef.getEvent().getId())
                    .collect(Collectors.toSet());
            model.addAttribute("favoritedIds", favIds);
        } else {
            model.addAttribute("favoritedIds", Set.of());
        }

        // Tập hợp các id event đã kết thúc
        LocalDateTime now = LocalDateTime.now();
        Set<Integer> expiredIds = paged.stream()
                .filter(e -> e.getEndTime() != null && e.getEndTime().isBefore(now))
                .map(e -> e.getId())
                .collect(Collectors.toSet());
        model.addAttribute("expiredIds", expiredIds);

        return "event/index";
    }

    @GetMapping("/{id}")
    public String eventDetail(@PathVariable Integer id,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model) {
        var eventDTO = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

        List<EventComment> comments = commentRepo.findVisibleCommentsWithUserByEventId(id);

        // Sự kiện liên quan: các sự kiện approved của cùng tổ chức, trừ sự kiện hiện tại
        List<com.tinhnguyenxanh.dto.EventDTO> relatedEvents = java.util.Collections.emptyList();
        if (eventDTO.getOrganizationId() != null) {
            relatedEvents = eventService.getEventsByOrganization(eventDTO.getOrganizationId())
                    .stream()
                    .filter(e -> !e.getId().equals(id)
                            && "approved".equalsIgnoreCase(e.getStatus())
                            && !e.isHidden())
                    .limit(4)
                    .collect(java.util.stream.Collectors.toList());
        }

        // Kiểm tra sự kiện đã kết thúc chưa
        boolean isExpired = eventDTO.getEndTime() != null
                && eventDTO.getEndTime().isBefore(java.time.LocalDateTime.now());

        model.addAttribute("event", eventDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedEvents", relatedEvents);
        model.addAttribute("isExpired", isExpired);
        model.addAttribute("registrationDTO", new EventRegistrationDTO());
        model.addAttribute("alreadyRegistered", false);
        model.addAttribute("isFavorited", false);
        model.addAttribute("volunteerFullName", "");
        model.addAttribute("volunteerPhone", "");

        if (userDetails != null) {
            try {
                boolean alreadyRegistered = registrationService.isAlreadyRegistered(id, userDetails.getId());
                boolean isFavorited = favoriteRepo.countByEventIdAndUserId(id, userDetails.getId()) > 0;
                model.addAttribute("alreadyRegistered", alreadyRegistered);
                model.addAttribute("isFavorited", isFavorited);

                volunteerRepo.findByUser_Id(userDetails.getId()).ifPresent(v -> {
                    model.addAttribute("volunteerFullName", v.getFullName());
                    model.addAttribute("volunteerPhone", v.getPhone());
                });
            } catch (Exception ex) {
                // Prevent a partial profile/favorite issue from breaking event details page.
                model.addAttribute("alreadyRegistered", false);
                model.addAttribute("isFavorited", false);
            }
        }
        return "event/details";
    }

    @PostMapping("/{id}/register")
    public String registerEvent(@PathVariable Integer id,
                                @Valid @ModelAttribute EventRegistrationDTO dto,
                                BindingResult result,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttrs) {
        if (userDetails == null) return "redirect:/auth/login";
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("error", "Vui lòng điền đầy đủ thông tin");
            return "redirect:/events/" + id;
        }
        dto.setEventId(id);
        boolean success = registrationService.register(dto, userDetails.getId());
        if (success) {
            redirectAttrs.addFlashAttribute("success", "Đăng ký thành công! Chờ xác nhận.");
        } else {
            redirectAttrs.addFlashAttribute("error", "Đăng ký thất bại. Bạn có thể đã đăng ký trước đó.");
        }
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/favorite")
    @Transactional
    public String toggleFavorite(@PathVariable Integer id,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes redirectAttrs) {
        if (userDetails == null) return "redirect:/auth/login";
        try {
            if (favoriteRepo.countByEventIdAndUserId(id, userDetails.getId()) > 0) {
                favoriteRepo.deleteByEventIdAndUserId(id, userDetails.getId());
                redirectAttrs.addFlashAttribute("success", "Đã xóa khỏi yêu thích");
            } else {
                EventFavorite fav = new EventFavorite();
                fav.setEvent(eventRepo.getReferenceById(id));
                fav.setUser(userRepo.getReferenceById(userDetails.getId()));
                fav.setFavoriteDate(LocalDateTime.now());
                favoriteRepo.save(fav);
                redirectAttrs.addFlashAttribute("success", "Đã thêm vào yêu thích");
            }
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("error", "Không thể cập nhật yêu thích. Vui lòng thử lại.");
        }
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/report")
    public String reportEvent(@PathVariable Integer id,
                              @RequestParam("reason") String reason,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              RedirectAttributes redirectAttrs) {
        if (userDetails == null) return "redirect:/auth/login";
        if (reason == null || reason.isBlank()) {
            redirectAttrs.addFlashAttribute("error", "Nội dung báo cáo không được để trống");
            return "redirect:/events/" + id;
        }
        if (reportRepo.existsByEvent_IdAndUser_Id(id, userDetails.getId())) {
            redirectAttrs.addFlashAttribute("error", "Bạn đã gửi báo cáo cho sự kiện này");
            return "redirect:/events/" + id;
        }

        Event event = eventService.getEventEntityById(id);
        User user = userRepo.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        EventReport report = EventReport.builder()
                .event(event)
                .user(user)
                .reportReason(reason.trim())
                .reportDate(LocalDateTime.now())
                .status("Pending")
                .build();
        reportRepo.save(report);

        redirectAttrs.addFlashAttribute("success", "Đã gửi báo cáo, admin sẽ xem xét sớm");
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Integer id,
                             @RequestParam String content,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttrs) {
        if (userDetails == null) return "redirect:/auth/login";
        if (content == null || content.isBlank()) {
            redirectAttrs.addFlashAttribute("error", "Bình luận không được để trống");
            return "redirect:/events/" + id;
        }

        Event event = eventService.getEventEntityById(id);
        User user = userRepo.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        EventComment comment = EventComment.builder()
                .event(event)
                .user(user)
                .content(content.trim())
                .createdAt(LocalDateTime.now())
                .isVisible(true)
                .isDeleted(false)
                .build();
        commentRepo.save(comment);

        redirectAttrs.addFlashAttribute("success", "Đã thêm bình luận");
        return "redirect:/events/" + id;
    }


    @PostMapping("/{id}/favorite/ajax")
    @Transactional
    @ResponseBody
    public ResponseEntity<Map<String,Object>> toggleFavoriteAjax(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null)
            return ResponseEntity.status(401).build();
        try {
            boolean favorited;
            if (favoriteRepo.countByEventIdAndUserId(id, userDetails.getId()) > 0) {
                favoriteRepo.deleteByEventIdAndUserId(id, userDetails.getId());
                favorited = false;
            } else {
                EventFavorite fav = new EventFavorite();
                fav.setEvent(eventRepo.getReferenceById(id));
                fav.setUser(userRepo.getReferenceById(userDetails.getId()));
                fav.setFavoriteDate(LocalDateTime.now());
                favoriteRepo.save(fav);
                favorited = true;
            }
            return ResponseEntity.ok(Map.of("favorited", favorited));
        } catch (Exception ex) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/my-registrations")
    public String myRegistrations(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        volunteerRepo.findByUser_Id(userDetails.getId()).ifPresent(v ->
                model.addAttribute("registrations", registrationService.getByVolunteerId(v.getId()))
        );
        return "event/my-registrations";
    }

    @GetMapping("/my-favorites")
    public String myFavorites(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        model.addAttribute("favorites", favoriteRepo.findByUser_Id(userDetails.getId()));
        return "event/my-favorites";
    }
}