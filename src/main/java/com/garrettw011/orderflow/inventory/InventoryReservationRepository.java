package com.garrettw011.orderflow.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.Instant;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {
    List<InventoryReservation> findByOrderId(Long orderId);
    List<InventoryReservation> findByStatusAndExpiresAtBefore(InventoryReservationStatus status, Instant cutoff);

    @Query("""
           SELECT DISTINCT r.order.id FROM InventoryReservation r
                WHERE r.status = :status AND r.expiresAt < :cutoff
           """)
    List<Long> findOrderIdsWithExpiredReservations(@Param("status") InventoryReservationStatus status,
                                                   @Param("cutoff") Instant cutoff);
}
