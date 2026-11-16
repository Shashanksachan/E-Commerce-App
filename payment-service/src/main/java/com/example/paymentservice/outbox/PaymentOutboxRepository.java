package com.example.paymentservice.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentOutboxRepository
        extends JpaRepository<PaymentOutbox, Long> {

    List<PaymentOutbox> findByStatus(String status);
}