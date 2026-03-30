package com.tinhnguyenxanh.repository;

import com.tinhnguyenxanh.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Integer> {
    Optional<Volunteer> findByUser_Id(Long userId);
    boolean existsByUser_Id(Long userId);
}
