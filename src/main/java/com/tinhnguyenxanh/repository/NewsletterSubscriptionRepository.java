package com.tinhnguyenxanh.repository;

import com.tinhnguyenxanh.entity.NewsletterSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsletterSubscriptionRepository extends JpaRepository<NewsletterSubscription, Long> {

    Optional<NewsletterSubscription> findByEmailIgnoreCase(String email);

    List<NewsletterSubscription> findAllByOrderBySubscribedAtDesc();

    long countByActiveTrue();
}