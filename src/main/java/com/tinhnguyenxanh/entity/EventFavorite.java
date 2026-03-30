package com.tinhnguyenxanh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_favorites")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@IdClass(EventFavorite.EventFavoriteId.class)
public class EventFavorite {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime favoriteDate = LocalDateTime.now();

    // ===== Composite Key Class =====
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventFavoriteId implements Serializable {
        private Integer event;
        private Long user;
    }
}
