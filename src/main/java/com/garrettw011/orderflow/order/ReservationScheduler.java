package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.inventory.InventoryReservationRepository;
import com.garrettw011.orderflow.inventory.InventoryReservationStatus;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.time.Instant;

@Component
public class ReservationScheduler {
    private static final Logger log = LoggerFactory.getLogger(ReservationScheduler.class);

    private final InventoryReservationRepository reservations;
    private final OrderProcessingService procService;

    public ReservationScheduler(InventoryReservationRepository reservations,
                                OrderProcessingService procService) {
        this.reservations = reservations;
        this.procService = procService;
    }

    @Scheduled(fixedDelayString = "${app.order.reservation-scan:1m}")
    @SchedulerLock(name = "releaseExpiredReservations", lockAtMostFor = "5m", lockAtLeastFor = "30s")
    public void releaseExpiredReservations() { reclaimExpired(); }

    public void reclaimExpired() {
        List<Long> orderIds = reservations.findOrderIdsWithExpiredReservations(
                InventoryReservationStatus.ACTIVE, Instant.now());

        for (Long orderId : orderIds) {
            try {
                procService.expireOrder(orderId);
            }
            catch (RuntimeException ex) {
                log.warn("Could not expire order {}", orderId, ex);
            }
        }
    }
}

