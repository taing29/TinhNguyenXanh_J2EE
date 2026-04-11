package com.tinhnguyenxanh.service;

import com.tinhnguyenxanh.entity.NewsletterSubscription;
import com.tinhnguyenxanh.repository.NewsletterSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    public enum SubscribeResult {
        CREATED,
        REACTIVATED,
        ALREADY_ACTIVE,
        INVALID
    }

    private final NewsletterSubscriptionRepository subscriptionRepo;

    @Transactional
    public SubscribeResult subscribe(String email) {
        if (email == null || email.isBlank()) {
            return SubscribeResult.INVALID;
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (!normalizedEmail.contains("@")) {
            return SubscribeResult.INVALID;
        }

        NewsletterSubscription existing = subscriptionRepo.findByEmailIgnoreCase(normalizedEmail).orElse(null);
        if (existing != null) {
            if (existing.isActive()) {
                return SubscribeResult.ALREADY_ACTIVE;
            }
            existing.setActive(true);
            existing.setSubscribedAt(LocalDateTime.now());
            subscriptionRepo.save(existing);
            return SubscribeResult.REACTIVATED;
        }

        subscriptionRepo.save(NewsletterSubscription.builder()
                .email(normalizedEmail)
                .active(true)
                .subscribedAt(LocalDateTime.now())
                .build());
        return SubscribeResult.CREATED;
    }

    public List<NewsletterSubscription> getAllSubscriptions() {
        return subscriptionRepo.findAllByOrderBySubscribedAtDesc();
    }

    public long countActiveSubscriptions() {
        return subscriptionRepo.countByActiveTrue();
    }

    @Transactional
    public boolean deactivate(Long id) {
        NewsletterSubscription subscription = subscriptionRepo.findById(id).orElse(null);
        if (subscription == null) {
            return false;
        }
        subscription.setActive(false);
        subscriptionRepo.save(subscription);
        return true;
    }

    @Transactional
    public boolean reactivate(Long id) {
        NewsletterSubscription subscription = subscriptionRepo.findById(id).orElse(null);
        if (subscription == null) {
            return false;
        }
        subscription.setActive(true);
        subscription.setSubscribedAt(LocalDateTime.now());
        subscriptionRepo.save(subscription);
        return true;
    }

    @Transactional
    public boolean deleteSubscription(Long id) {
        if (!subscriptionRepo.existsById(id)) {
            return false;
        }
        subscriptionRepo.deleteById(id);
        return true;
    }
}