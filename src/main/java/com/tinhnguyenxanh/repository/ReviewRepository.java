package com.tinhnguyenxanh.repository;

import com.tinhnguyenxanh.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByOrganization_Id(Integer organizationId);
    boolean existsByUser_IdAndOrganization_Id(Long userId, Integer organizationId);
}
