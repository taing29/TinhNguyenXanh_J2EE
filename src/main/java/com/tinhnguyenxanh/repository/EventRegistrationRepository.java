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
}
