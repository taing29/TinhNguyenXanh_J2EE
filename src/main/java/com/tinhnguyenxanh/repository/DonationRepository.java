package com.tinhnguyenxanh.repository;

import com.tinhnguyenxanh.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Integer> {

    Optional<Donation> findByTransactionCode(String transactionCode);
}
