package com.garrettw011.orderflow.report;

import com.garrettw011.orderflow.order.OrderStatus;
import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReportApiTest extends AbstractIntegrationTest {

    private String today() { return LocalDate.now(ZoneOffset.UTC).toString(); }

    private BigDecimal revenueToday() throws Exception {
        String body = mvc.perform(get("/api/v1/admin/reports/revenue?from=" + today() + "&to=" + today())
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return new BigDecimal(JsonPath.read(body, "$.totalRevenue").toString());
    }

    private long orderCountByStatus(OrderStatus status) throws Exception {
        String body = mvc.perform(get("/api/v1/admin/reports/order-status-counts")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Integer> count = JsonPath.read(body, "$[?(@.status=='%s')].count".formatted(status.toStr()));
        return count.isEmpty() ? 0 : count.getFirst().longValue();
    }

    @Test
    void revenueIncreasesByCapturedAmount() throws Exception {
        BigDecimal before = revenueToday();
        long pid = createStockedProduct("RPT-REV", "10.00", 100);
        payOrder(placeOrder(pid, 3));    // subtotal of 30.00 + tax of 2.40 (30.00 * 0.08 = 2.40) is 32.40 total

        BigDecimal after = revenueToday();
        assertThat(after.subtract(before)).isEqualByComparingTo("32.40");
    }

    @Test
    void topProductsGivesSoldCount() throws Exception {
        long pid = createStockedProduct("RPT-TOP", "5.00", 100);
        payOrder(placeOrder(pid, 7));

        String body = mvc.perform(get("/api/v1/admin/reports/top-products?from=" +
                                today() + "&to=" + today() + "&limit=100")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<Integer> units = JsonPath.read(body, "$[?(@.sku=='RPT-TOP')].unitsSold");
        assertThat(units).containsExactly(7);
    }

    @Test
    void ordersIncreaseOrderStatusCounts() throws Exception {
        long before = orderCountByStatus(OrderStatus.RESERVED);
        long pid = createStockedProduct("RPT-STS", "10.00", 100);
        placeOrder(pid, 1);
        assertThat(orderCountByStatus(OrderStatus.RESERVED)).isEqualTo(before + 1);
    }

    @Test
    void lowStockShowsThresholdItems() throws Exception {
        String body = mvc.perform(get("/api/v1/admin/reports/low-stock")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<String> skus = JsonPath.read(body, "$[*].sku");
        // verify low stock report shows demo item from demo data migration added
        // with on hand qty of 5 and threshold of 5 (sku:"MXS-CHR-ERG-001")
        assertThat(skus).contains("MXS-CHR-ERG-001");
    }

    @Test
    void invalidDateRangeReturns400() throws Exception {
        mvc.perform(get("/api/v1/admin/reports/revenue?from=2026-12-31&to=2026-01-01")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reportsRequireAdmin() throws Exception {
        mvc.perform(get("/api/v1/admin/reports/revenue").header("Authorization", bearer(token())))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/admin/reports/revenue"))
                .andExpect(status().isUnauthorized());
    }
}

