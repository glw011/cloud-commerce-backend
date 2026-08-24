package com.garrettw011.orderflow.observability;

import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.Search;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ObservabilityTest extends AbstractIntegrationTest {

    @Autowired
    MeterRegistry registry;

    // returns a counter's curr val (unregistered = 0.0)
    //   * 'tags' alternate as: "key", "val", "key", "val", ...
    private double counter(String name, String... tags) {
        Search search = registry.find(name);

        if (tags.length > 0) {
            search = search.tags(tags);
        }
        Counter c = search.counter();

        return c == null ? 0.0 : c.count();
    }

    @Test
    void placingOrderIncrementsCounter() throws Exception {
        double before = counter("orderflow.orders.placed");

        long pid = createStockedProduct("TST-OBS-1", "19.99", 5);
        placeOrder(pid, 1);

        double after = counter("orderflow.orders.placed");

        assertThat(after - before).isEqualTo(1.0);
    }

    @Test
    void capturedPaymentIncrementsCounter() throws Exception {
        double before = counter("orderflow.payments", "outcome", "captured");

        long pid = createStockedProduct("TST-OBS-2", "29.99", 5);
        long order = placeOrder(pid, 1);
        mvc.perform(post("/api/v1/orders/" + order + "/payments")
                        .header("Authorization", bearer(token()))
                        .contentType(json).content(payBody()))
                .andExpect(status().isCreated());

        double after = counter("orderflow.payments", "outcome", "captured");

        assertThat(after - before).isEqualTo(1.0);
    }

    @Test
    void livenessAndReadinessReportUp() throws Exception {
        mvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}