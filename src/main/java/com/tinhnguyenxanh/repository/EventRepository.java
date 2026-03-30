package com.tinhnguyenxanh.repository;

import com.tinhnguyenxanh.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByOrganizationId(Integer organizationId);

    List<Event> findByStatusIgnoreCase(String status);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.organization LEFT JOIN FETCH e.category WHERE e.id = :id")
    java.util.Optional<Event> findByIdWithDetails(@Param("id") Integer id);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.organization LEFT JOIN FETCH e.category")
    List<Event> findAllWithDetails();

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.organization LEFT JOIN FETCH e.category WHERE LOWER(e.status) = 'approved' AND e.isHidden = false")
    List<Event> findAllApproved();

    List<Event> findByOrganization_IdAndStatusIgnoreCase(Integer organizationId, String status);
}
