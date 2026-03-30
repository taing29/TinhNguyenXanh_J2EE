package com.tinhnguyenxanh.repository;

import com.tinhnguyenxanh.entity.EventReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventReportRepository extends JpaRepository<EventReport, Integer> {
    List<EventReport> findByEvent_Id(Integer eventId);
    List<EventReport> findByStatus(String status);
    boolean existsByEvent_IdAndUser_Id(Integer eventId, Long userId);

    @Query("""
        SELECT r
        FROM EventReport r
        JOIN FETCH r.event e
        JOIN FETCH r.user u
        WHERE LOWER(r.status) = 'pending'
        ORDER BY r.reportDate DESC
    """)
    List<EventReport> findPendingWithDetails();
}
