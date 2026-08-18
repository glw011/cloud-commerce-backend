package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.inventory.InventoryReservation;
import com.garrettw011.orderflow.inventory.InventoryReservationRepository;
import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderWorkflowTest extends AbstractIntegrationTest {

    @Autowired InventoryReservationRepository reservations;
    @Autowired ReservationScheduler scheduler;

    private String pay(long orderId, String token) {
        return """
            {"provider":"stripe","paymentToken":"%s"}""".formatted(token);
    }

    @Test
    void payingConsumesStockAndMarksOrderPaid() throws Exception {
        long pid = createStockedProduct("LC-P1", "10.00", 100);
        long orderId = placeOrder(pid, 4);

        mvc.perform(post("/api/v1/orders/" + orderId + "/payments")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(pay(orderId, "tok_ok")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CAPTURED"))
                .andExpect(jsonPath("$.orderStatus").value("PAID"))
                .andExpect(jsonPath("$.transactionReference").exists());

        mvc.perform(get("/api/v1/inventory/" + pid).header("Authorization", bearer(adminToken())))
                .andExpect(jsonPath("$.quantityOnHand").value(96))
                .andExpect(jsonPath("$.quantityReserved").value(0))
                .andExpect(jsonPath("$.availableQuantity").value(96));
    }

    @Test
    void declinedPaymentReleasesStockAndFailsOrder() throws Exception {
        long pid = createStockedProduct("LC-P2", "10.00", 100);
        long orderId = placeOrder(pid, 4);

        mvc.perform(post("/api/v1/orders/" + orderId + "/payments")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(pay(orderId, "fail_card")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.orderStatus").value("FAILED"));

        mvc.perform(get("/api/v1/inventory/" + pid)
                        .header("Authorization", bearer(adminToken())))
                .andExpect(jsonPath("$.quantityOnHand").value(100))
                .andExpect(jsonPath("$.quantityReserved").value(0));
    }

    @Test
    void cannotPayAnAlreadyPaidOrder() throws Exception {
        long pid = createStockedProduct("LC-P3", "10.00", 100);
        long orderId = placeOrder(pid, 1);
        mvc.perform(post("/api/v1/orders/" + orderId + "/payments")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(pay(orderId, "tok_ok")))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/orders/" + orderId + "/payments")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(pay(orderId, "tok_ok")))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelingReleasesStock() throws Exception {
        long pid = createStockedProduct("LC-P4", "10.00", 100);
        long orderId = placeOrder(pid, 6);

        mvc.perform(post("/api/v1/orders/" + orderId + "/cancel")
                        .header("Authorization", bearer(token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));

        mvc.perform(get("/api/v1/inventory/" + pid)
                        .header("Authorization", bearer(adminToken())))
                .andExpect(jsonPath("$.quantityReserved").value(0))
                .andExpect(jsonPath("$.availableQuantity").value(100));
    }

    @Test
    void fulfillmentFlowRequiresPaidAndTheRightRole() throws Exception {
        long pid = createStockedProduct("LC-P5", "10.00", 100);
        long orderId = placeOrder(pid, 1);

        mvc.perform(post("/api/v1/orders/" + orderId + "/fulfill")
                        .header("Authorization", bearer(managerToken())))
                .andExpect(status().isConflict());

        mvc.perform(post("/api/v1/orders/" + orderId + "/payments")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(pay(orderId, "tok_ok")))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/orders/" + orderId + "/fulfill")
                        .header("Authorization", bearer(token())))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/v1/orders/" + orderId + "/fulfill")
                        .header("Authorization", bearer(managerToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FULFILLING"));

        mvc.perform(post("/api/v1/orders/" + orderId + "/ship")
                        .header("Authorization", bearer(managerToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void listingPaymentsReturnsAttempt() throws Exception {
        long pid = createStockedProduct("LC-P6", "10.00", 100);
        long orderId = placeOrder(pid, 1);

        mvc.perform(post("/api/v1/orders/" + orderId + "/payments")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(pay(orderId, "tok_ok")))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/orders/" + orderId + "/payments")
                        .header("Authorization", bearer(token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("CAPTURED"));
    }

    @Test
    void schedulerReleasesStaleReservations() throws Exception {
        long pid = createStockedProduct("LC-P7", "10.00", 100);
        long orderId = placeOrder(pid, 5);

        List<InventoryReservation> rs = reservations.findByOrderId(orderId);

        rs.forEach(r -> r.setExpiresAt(Instant.now().minusSeconds(3600)));

        reservations.saveAll(rs);

        scheduler.reclaimExpired();

        mvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", bearer(adminToken())))
                .andExpect(jsonPath("$.status").value("CANCELED"));

        mvc.perform(get("/api/v1/inventory/" + pid)
                        .header("Authorization", bearer(adminToken())))
                .andExpect(jsonPath("$.quantityReserved").value(0))
                .andExpect(jsonPath("$.availableQuantity").value(100));
    }
}