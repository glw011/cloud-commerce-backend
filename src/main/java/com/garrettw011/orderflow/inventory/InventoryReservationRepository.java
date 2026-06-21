package com.garrettw011.orderflow.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.Instant;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {
    List<InventoryReservation> findByOrderId(Long orderId);
    List<InventoryReservation> findByStatusAndExpiresAtBefore(InventoryReservationStatus status, Instant cutoff);
}
