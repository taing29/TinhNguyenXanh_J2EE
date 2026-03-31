package com.tinhnguyenxanh.service;

import com.tinhnguyenxanh.dto.EventDTO;
import com.tinhnguyenxanh.dto.EventRegistrationDTO;
import com.tinhnguyenxanh.entity.*;
import com.tinhnguyenxanh.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepo;
    private final EventRegistrationRepository registrationRepo;
    private final VolunteerRepository volunteerRepo;
    private final EventCategoryRepository categoryRepo;
    private final FileUploadService fileUploadService;

    public List<EventDTO> getAllEvents() {
        return eventRepo.findAllWithDetails().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getApprovedEvents() {
        return eventRepo.findAllApproved().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByOrganization(Integer orgId) {
        return eventRepo.findByOrganizationId(orgId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventDTO> getEventById(Integer id) {
        return eventRepo.findByIdWithDetails(id).map(e -> {
            EventDTO dto = toDTO(e);
            int confirmed = registrationRepo.countByEvent_IdAndStatusIgnoreCase(id, "Confirmed");
            dto.setRegisteredCount(confirmed);
            return dto;
        });
    }

    public Event getEventEntityById(Integer id) {
        return eventRepo.findById(id).orElseThrow(() ->
                new RuntimeException("Không tìm thấy sự kiện với id: " + id));
    }

    @Transactional
    public boolean createEvent(EventDTO dto, Integer organizationId) {
        try {
            String imagePath = null;
            if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
                imagePath = fileUploadService.uploadFile(dto.getImageFile(), "events");
            }

            Event event = Event.builder()
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .location(dto.getLocation())
                    .locationCoords(dto.getLocationCoords())
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .maxVolunteers(dto.getMaxVolunteers())
                    .status("pending")
                    .images(imagePath)
                    .build();

            if (organizationId != null) {
                event.setOrganization(new Organization());
                event.getOrganization().setId(organizationId);
            }
            if (dto.getCategoryId() != null) {
                event.setCategory(categoryRepo.findById(dto.getCategoryId()).orElse(null));
            }

            eventRepo.save(event);
            return true;
        } catch (Exception e) {
            System.err.println("[CREATE EVENT ERROR] " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean updateEvent(EventDTO dto, Integer organizationId) {
        if (dto.getId() == null) return false;
        Event existing = eventRepo.findById(dto.getId()).orElse(null);
        if (existing == null) return false;
        if (organizationId != null && !organizationId.equals(existing.getOrganization().getId())) return false;

        try {
            String imagePath = existing.getImages();
            if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
                // Delete old image
                if (imagePath != null) fileUploadService.deleteFile(imagePath);
                imagePath = fileUploadService.uploadFile(dto.getImageFile(), "events");
            }

            existing.setTitle(dto.getTitle());
            existing.setDescription(dto.getDescription());
            existing.setLocation(dto.getLocation());
            existing.setLocationCoords(dto.getLocationCoords());
            existing.setStartTime(dto.getStartTime());
            existing.setEndTime(dto.getEndTime());
            existing.setMaxVolunteers(dto.getMaxVolunteers());
            existing.setImages(imagePath);

            if (dto.getCategoryId() != null) {
                existing.setCategory(categoryRepo.findById(dto.getCategoryId()).orElse(null));
            }

            eventRepo.save(existing);
            return true;
        } catch (Exception e) {
            System.err.println("[UPDATE EVENT ERROR] " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean approveEvent(Integer eventId) {
        Event event = eventRepo.findById(eventId).orElse(null);
        if (event == null) return false;
        event.setStatus("approved");
        eventRepo.save(event);
        return true;
    }

    @Transactional
    public boolean rejectEvent(Integer eventId, String reason) {
        Event event = eventRepo.findById(eventId).orElse(null);
        if (event == null) return false;
        event.setStatus("rejected");
        event.setHidden(true);
        event.setHiddenReason(reason);
        eventRepo.save(event);
        return true;
    }

    @Transactional
    public boolean toggleHidden(Integer eventId, Integer organizationId) {
        Event event = eventRepo.findById(eventId).orElse(null);
        if (event == null) return false;
        if (organizationId != null && !organizationId.equals(event.getOrganization().getId())) return false;
        event.setHidden(!event.isHidden());
        if (event.isHidden()) event.setHiddenAt(java.time.LocalDateTime.now());
        else event.setHiddenAt(null);
        eventRepo.save(event);
        return true;
    }

    @Transactional
    public boolean deleteEvent(Integer eventId) {
        if (!eventRepo.existsById(eventId)) return false;
        eventRepo.deleteById(eventId);
        return true;
    }

    private EventDTO toDTO(Event e) {
        return EventDTO.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .status(e.getStatus())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .location(e.getLocation())
                .locationCoords(e.getLocationCoords())
                .organizationId(e.getOrganization() != null ? e.getOrganization().getId() : null)
                .organizationName(e.getOrganization() != null ? e.getOrganization().getName() : "Unknown")
                .categoryId(e.getCategory() != null ? e.getCategory().getId() : null)
                .categoryName(e.getCategory() != null ? e.getCategory().getName() : "Chưa phân loại")
                .maxVolunteers(e.getMaxVolunteers())
                .registeredCount(registrationRepo.countByEvent_IdAndStatusIgnoreCase(e.getId(), "Confirmed"))
                .images(e.getImages())
                .isHidden(e.isHidden())
                .hiddenReason(e.getHiddenReason())
                .build();
    }
}
