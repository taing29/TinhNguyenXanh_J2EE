package com.tinhnguyenxanh.service;

import com.tinhnguyenxanh.dto.EventRegistrationDTO;
import com.tinhnguyenxanh.entity.*;
import com.tinhnguyenxanh.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepo;
    private final VolunteerRepository volunteerRepo;
    private final EventRepository eventRepo;

    @Transactional
    public boolean register(EventRegistrationDTO dto, Long userId) {
        Volunteer volunteer = volunteerRepo.findByUser_Id(userId).orElse(null);
        if (volunteer == null) return false;

        if (registrationRepo.existsByEvent_IdAndVolunteer_Id(dto.getEventId(), volunteer.getId()))
            return false;

        Event event = eventRepo.findById(dto.getEventId()).orElse(null);
        if (event == null || !"approved".equalsIgnoreCase(event.getStatus())) return false;

        EventRegistration reg = EventRegistration.builder()
                .event(event)
                .volunteer(volunteer)
                .fullName(dto.getFullName())
                .phone(dto.getPhone())
                .reason(dto.getReason())
                .status("Pending")
                .registeredDate(LocalDateTime.now())
                .build();

        registrationRepo.save(reg);
        return true;
    }

    public List<EventRegistration> getByEventId(Integer eventId) {
        return registrationRepo.findByEvent_Id(eventId);
    }

    public List<EventRegistration> getByVolunteerId(Integer volunteerId) {
        return registrationRepo.findByVolunteerIdWithEvent(volunteerId);
    }

    public List<EventRegistration> getAllRegistrations() {
        return registrationRepo.findAllWithEventAndVolunteer();
    }

    public Optional<EventRegistration> getById(Integer id) {
        return registrationRepo.findById(id);
    }

    @Transactional
    public boolean approve(Integer id) {
        EventRegistration reg = registrationRepo.findById(id).orElse(null);
        if (reg == null || !"Pending".equals(reg.getStatus())) return false;

        // Kiểm tra số lượng TNV đã xác nhận có vượt quá maxVolunteers chưa
        Event event = reg.getEvent();
        int confirmed = registrationRepo.countByEvent_IdAndStatusIgnoreCase(event.getId(), "Confirmed");
        if (confirmed >= event.getMaxVolunteers()) return false;

        reg.setStatus("Confirmed");
        registrationRepo.save(reg);
        return true;
    }

    @Transactional
    public boolean reject(Integer id) {
        EventRegistration reg = registrationRepo.findById(id).orElse(null);
        if (reg == null || !"Pending".equals(reg.getStatus())) return false;
        reg.setStatus("Rejected");
        registrationRepo.save(reg);
        return true;
    }

    public boolean isAlreadyRegistered(Integer eventId, Long userId) {
        return volunteerRepo.findByUser_Id(userId)
                .map(v -> registrationRepo.existsByEvent_IdAndVolunteer_Id(eventId, v.getId()))
                .orElse(false);
    }

    public int countConfirmed(Integer eventId) {
        return registrationRepo.countByEvent_IdAndStatusIgnoreCase(eventId, "Confirmed");
    }

    public int countPending() {
        return (int) registrationRepo.findAll().stream()
                .filter(r -> "Pending".equalsIgnoreCase(r.getStatus()))
                .count();
    }

    public int countConfirmedAll() {
        return (int) registrationRepo.findAll().stream()
                .filter(r -> "Confirmed".equalsIgnoreCase(r.getStatus()))
                .count();
    }

    public int countRejectedAll() {
        return (int) registrationRepo.findAll().stream()
                .filter(r -> "Rejected".equalsIgnoreCase(r.getStatus()))
                .count();
    }
}