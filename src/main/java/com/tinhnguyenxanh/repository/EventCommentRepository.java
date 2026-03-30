package com.tinhnguyenxanh.repository;

import com.tinhnguyenxanh.entity.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventCommentRepository extends JpaRepository<EventComment, Integer> {
    @Query("""
        SELECT c
        FROM EventComment c
        JOIN FETCH c.user u
        WHERE c.event.id = :eventId
          AND c.isDeleted = false
          AND c.isVisible = true
        ORDER BY c.createdAt DESC
    """)
    List<EventComment> findVisibleCommentsWithUserByEventId(@Param("eventId") Integer eventId);
}
