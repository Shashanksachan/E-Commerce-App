package com.example.inventoryservice.repository;

import com.example.inventoryservice.entity.InventoryOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryOutboxRepository
        extends JpaRepository<InventoryOutbox, Long> {

    List<InventoryOutbox> findByStatus(String status);

}