package com.tinhnguyenxanh.repository;

import com.tinhnguyenxanh.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Integer> {

    List<EventRegistration> findByEvent_Id(Integer eventId);

    List<EventRegistration> findByVolunteer_Id(Integer volunteerId);

    boolean existsByEvent_IdAndVolunteer_Id(Integer eventId, Integer volunteerId);

    int countByEvent_IdAndStatusIgnoreCase(Integer eventId, String status);

    int countByVolunteer_Id(Integer volunteerId);

    int countByVolunteer_IdAndStatusIgnoreCase(Integer volunteerId, String status);

    Optional<EventRegistration> findByEvent_IdAndVolunteer_Id(Integer eventId, Integer volunteerId);

    @Query("SELECT r FROM EventRegistration r JOIN FETCH r.event WHERE r.volunteer.id = :vId ORDER BY r.registeredDate DESC")
    List<EventRegistration> findByVolunteerIdWithEvent(@Param("vId") Integer volunteerId);

    @Query("SELECT r FROM EventRegistration r JOIN FETCH r.event JOIN FETCH r.volunteer ORDER BY r.registeredDate DESC")
    List<EventRegistration> findAllWithEventAndVolunteer();

    @Query("SELECT r FROM EventRegistration r JOIN FETCH r.volunteer JOIN FETCH r.event e JOIN FETCH e.organization o WHERE r.volunteer.id = :vId AND o.id = :orgId ORDER BY r.registeredDate DESC")
    List<EventRegistration> findByVolunteerIdAndOrganizationId(@Param("vId") Integer volunteerId, @Param("orgId") Integer orgId);

    @Query("SELECT r FROM EventRegistration r JOIN FETCH r.event e JOIN FETCH r.volunteer v WHERE e.organization.id = :orgId ORDER BY r.registeredDate DESC")
    List<EventRegistration> findAllByOrganizationId(@Param("orgId") Integer orgId);

    @Query("SELECT r FROM EventRegistration r JOIN FETCH r.volunteer JOIN FETCH r.event e JOIN FETCH e.organization WHERE e.organization.id = :orgId AND LOWER(r.status) = LOWER(:status) ORDER BY r.registeredDate ASC")
    List<EventRegistration> findByOrganizationIdAndStatusIgnoreCase(@Param("orgId") Integer orgId, @Param("status") String status);

    @Query("SELECT COUNT(r) FROM EventRegistration r JOIN r.event e WHERE e.organization.id = :orgId AND LOWER(r.status) = 'pending'")
    long countPendingByOrganizationId(@Param("orgId") Integer orgId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM EventRegistration r JOIN r.event e WHERE r.volunteer.id = :vId AND e.organization.id = :orgId")
    boolean existsVolunteerInOrganization(@Param("vId") Integer volunteerId, @Param("orgId") Integer orgId);

    @Query("SELECT r FROM EventRegistration r JOIN FETCH r.event e JOIN FETCH e.organization WHERE r.id = :id")
    Optional<EventRegistration> findByIdWithEventAndOrganization(@Param("id") Integer id);
}
