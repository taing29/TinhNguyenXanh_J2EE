package com.tinhnguyenxanh.service;

import com.tinhnguyenxanh.dto.OrganizationDTO;
import com.tinhnguyenxanh.entity.*;
import com.tinhnguyenxanh.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository orgRepo;
    private final UserRepository userRepo;
    private final FileUploadService fileUploadService;
    private final com.tinhnguyenxanh.repository.ReviewRepository reviewRepo;

    public List<OrganizationDTO> getAllVerified() {
        return orgRepo.findByIsApprovedTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<Organization> getPendingApproval() {
        return orgRepo.findByIsApprovedFalse();
    }

    public Optional<OrganizationDTO> getById(Integer id) {
        return orgRepo.findByIdWithDetails(id).map(this::toDTO);
    }

    public Optional<Organization> getEntityById(Integer id) {
        return orgRepo.findById(id);
    }

    public Optional<Organization> getByUserId(Long userId) {
        return orgRepo.findByUser_Id(userId);
    }

    @Transactional
    public boolean register(OrganizationDTO dto, Long userId) {
        try {
            if (orgRepo.existsByUser_Id(userId))
                throw new IllegalStateException("Bạn đã đăng ký tổ chức trước đó");

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng"));

            String avatarUrl = null;
            if (dto.getAvatarFile() != null && !dto.getAvatarFile().isEmpty()) {
                avatarUrl = fileUploadService.uploadFile(dto.getAvatarFile(), "organizations");
            }

            Organization org = Organization.builder()
                    .user(user)
                    .name(dto.getName().trim())
                    .organizationType(dto.getOrganizationType().trim())
                    .description(dto.getDescription().trim())
                    .focusAreas(String.join(",", dto.getFocusAreas()))
                    .avatarUrl(avatarUrl)
                    .contactEmail(dto.getContactEmail().trim())
                    .phoneNumber(dto.getPhoneNumber().trim())
                    .website(dto.getWebsite())
                    .address(dto.getAddress() != null ? dto.getAddress().trim() : "")
                    .city(dto.getCity())
                    .district(dto.getDistrict())
                    .ward(dto.getWard())
                    .taxCode(dto.getTaxCode())
                    .foundedDate(dto.getFoundedDate())
                    .legalRepresentative(dto.getLegalRepresentative())
                    .verificationDocsUrl(dto.getVerificationDocsUrl())
                    .documentType(dto.getDocumentType())
                    .verified(true)
                    .facebookUrl(dto.getFacebookUrl())
                    .zaloNumber(dto.getZaloNumber())
                    .memberCount(dto.getMemberCount())
                    .eventsOrganized(dto.getEventsOrganized())
                    .achievements(dto.getAchievements())
                    .joinedDate(LocalDateTime.now())
                    .lastUpdated(LocalDateTime.now())
                    .isActive(true)
                    .isApproved(false)
                    .build();

            orgRepo.save(org);
            return true;
        } catch (Exception e) {
            System.err.println("[ORG REGISTER ERROR] " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean approve(Integer orgId) {
        Organization org = orgRepo.findById(orgId).orElse(null);
        if (org == null) return false;
        org.setApproved(true);
        org.setVerifiedDate(LocalDateTime.now());

        // Gán role ORGANIZER cho user
        User user = org.getUser();
        if (user != null) {
            user.setRole(com.tinhnguyenxanh.enums.Role.ORGANIZER);
            userRepo.save(user);
        }
        orgRepo.save(org);
        return true;
    }

    @Transactional
    public boolean reject(Integer orgId, String notes) {
        Organization org = orgRepo.findById(orgId).orElse(null);
        if (org == null) return false;
        org.setVerified(false);
        org.setVerificationNotes(notes);
        orgRepo.save(org);
        return true;
    }

    @Transactional
    public boolean update(OrganizationDTO dto, Long userId) {
        Organization org = orgRepo.findByUser_Id(userId).orElse(null);
        if (org == null) return false;

        try {
            if (dto.getAvatarFile() != null && !dto.getAvatarFile().isEmpty()) {
                if (org.getAvatarUrl() != null) fileUploadService.deleteFile(org.getAvatarUrl());
                org.setAvatarUrl(fileUploadService.uploadFile(dto.getAvatarFile(), "organizations"));
            }
            org.setName(dto.getName().trim());
            org.setDescription(dto.getDescription().trim());
            org.setContactEmail(dto.getContactEmail().trim());
            org.setPhoneNumber(dto.getPhoneNumber().trim());
            org.setWebsite(dto.getWebsite());
            org.setAddress(dto.getAddress());
            org.setCity(dto.getCity());
            org.setDistrict(dto.getDistrict());
            org.setFacebookUrl(dto.getFacebookUrl());
            org.setZaloNumber(dto.getZaloNumber());
            org.setLastUpdated(LocalDateTime.now());
            if (dto.getFocusAreas() != null)
                org.setFocusAreas(String.join(",", dto.getFocusAreas()));

            orgRepo.save(org);
            return true;
        } catch (Exception e) {
            System.err.println("[ORG UPDATE ERROR] " + e.getMessage());
            return false;
        }
    }

    public OrganizationDTO toPublicDTO(Organization o) {
        return toDTO(o);
    }

    private OrganizationDTO toDTO(Organization o) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(o.getId());
        dto.setUserId(o.getUser() != null ? o.getUser().getId() : null);
        dto.setName(o.getName());
        dto.setOrganizationType(o.getOrganizationType());
        dto.setDescription(o.getDescription());
        dto.setAvatarUrl(o.getAvatarUrl());
        dto.setContactEmail(o.getContactEmail());
        dto.setPhoneNumber(o.getPhoneNumber());
        dto.setWebsite(o.getWebsite());
        dto.setAddress(o.getAddress());
        dto.setCity(o.getCity());
        dto.setDistrict(o.getDistrict());
        dto.setWard(o.getWard());
        dto.setFacebookUrl(o.getFacebookUrl());
        dto.setInstagramUrl(o.getInstagramUrl());
        dto.setZaloNumber(o.getZaloNumber());
        dto.setMemberCount(o.getMemberCount());
        dto.setEventsOrganized(o.getEventsOrganized());
        dto.setAchievements(o.getAchievements());
        dto.setVerified(o.isVerified());
        dto.setApproved(o.isApproved());
        dto.setJoinedDate(o.getJoinedDate());
        // Compute live from reviews (stored field may be stale)
        if (o.getReviews() != null && !o.getReviews().isEmpty()) {
            double avg = o.getReviews().stream().mapToInt(r -> r.getRating()).average().orElse(0);
            dto.setAverageRating(java.math.BigDecimal.valueOf(avg).setScale(1, java.math.RoundingMode.HALF_UP));
            dto.setTotalReviews(o.getReviews().size());
        } else {
            // fallback to DB-stored value
            dto.setAverageRating(o.getAverageRating());
            dto.setTotalReviews(o.getTotalReviews() != null ? o.getTotalReviews() : 0);
        }
        dto.setFoundedDate(o.getFoundedDate());
        dto.setTaxCode(o.getTaxCode());
        dto.setLegalRepresentative(o.getLegalRepresentative());
        if (o.getFocusAreas() != null && !o.getFocusAreas().isBlank()) {
            dto.setFocusAreas(Arrays.asList(o.getFocusAreas().split(",")));
        }
        return dto;
    }
}
