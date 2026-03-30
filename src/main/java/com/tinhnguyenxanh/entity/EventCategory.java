package com.tinhnguyenxanh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "event_categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên danh mục là bắt buộc")
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Event> events;
}
