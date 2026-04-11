package com.tinhnguyenxanh.repository;

import com.tinhnguyenxanh.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {

    Optional<Organization> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);

    List<Organization> findByVerifiedTrue();

    List<Organization> findByIsApprovedTrue();

    List<Organization> findByIsApprovedFalse();

    @Query("SELECT o FROM Organization o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.reviews r LEFT JOIN FETCH r.user WHERE o.id = :id")
    Optional<Organization> findByIdWithDetails(@Param("id") Integer id);
}
