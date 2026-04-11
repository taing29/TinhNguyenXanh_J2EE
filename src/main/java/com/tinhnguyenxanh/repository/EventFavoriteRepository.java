package com.tinhnguyenxanh.repository;

import com.tinhnguyenxanh.entity.EventFavorite;
import com.tinhnguyenxanh.entity.EventFavorite.EventFavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventFavoriteRepository extends JpaRepository<EventFavorite, EventFavoriteId> {

        @Query("SELECT DISTINCT ef FROM EventFavorite ef " +
            "JOIN FETCH ef.event e " +
            "LEFT JOIN FETCH e.category " +
            "LEFT JOIN FETCH e.organization " +
            "WHERE ef.user.id = :userId")
    List<EventFavorite> findByUser_Id(@Param("userId") Long userId);

    /** JPQL: derived delete/exists không ổn định với @IdClass + @ManyToOne làm khóa */
    @Query("SELECT COUNT(ef) FROM EventFavorite ef WHERE ef.event.id = :eventId AND ef.user.id = :userId")
    long countByEventIdAndUserId(@Param("eventId") Integer eventId, @Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM EventFavorite ef WHERE ef.event.id = :eventId AND ef.user.id = :userId")
    void deleteByEventIdAndUserId(@Param("eventId") Integer eventId, @Param("userId") Long userId);
}
