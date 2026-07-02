package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderApiTest extends AbstractIntegrationTest {
    private long stockNewProduct(String sku, String price, int qty) throws Exception {
        String created = mvc.perform(post("/api/v1/products")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(json)
                        .content(productBody(sku, "Test ".concat(sku), price)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long pid = ((Number) JsonPath.read(created, "$.id")).longValue();
        mvc.perform(patch("/api/v1/inventory/" + pid + "/adjust")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(json)
                        .content("{\"delta\":" + qty + "}"))
                .andExpect(status().isOk());
        return pid;
    }

    @Test
    void placeOrderComputesStockQty() throws Exception {
        long pid = stockNewProduct("ORD-TST-P01", "10.00", 100);

        mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(orderBody(pid, 2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RESERVED"))
                .andExpect(jsonPath("$.subtotal").value(20.00))
                .andExpect(jsonPath("$.tax").value(1.60))
                .andExpect(jsonPath("$.total").value(21.60))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].unitPrice").value(10.00))
                .andExpect(jsonPath("$.items[0].lineTotal").value(20.00));

        mvc.perform(get("/api/v1/inventory/" + pid)
                        .header("Authorization", bearer(adminToken())))
                .andExpect(jsonPath("$.quantityReserved").value(2))
                .andExpect(jsonPath("$.availableQuantity").value(98));
    }

    @Test
    void dupOrderItemsAreMerged() throws Exception {
        long pid = stockNewProduct("ORD-TST-P02", "5.00", 100);
        String body = """
                       {"items":[%s,%s]}""".formatted(orderItemBody(pid, 2), orderItemBody(pid, 3));

        mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(5))
                .andExpect(jsonPath("$.subtotal").value(25.00));
    }

    @Test
    void qtyExceedsStockReturns409() throws Exception {
        long pid = stockNewProduct("ORD-TST-P03", "10.00", 1);
        mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(orderBody(pid, 5)))
                .andExpect(status().isConflict());
    }

    @Test
    void badProductIdReturns404() throws Exception {
        mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(orderBody(999_999L, 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void inactiveProductReturns404() throws Exception {
        long pid = stockNewProduct("ORD-TST-P04", "10.00", 10);
        mvc.perform(patch("/api/v1/products/" + pid + "/deactivate")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk());

        mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(orderBody(pid, 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void snapshotPreservesOrderPrice() throws Exception {
        // create order
        long pid = stockNewProduct("ORD-TST-P05", "10.00", 100);
        String order = mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token()))
                        .contentType(json)
                        .content(orderBody(pid, 1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long orderId = ((Number) JsonPath.read(order, "$.id")).longValue();

        // raise item price
        String change = """
                      {"name":"ORD-TST-P05","price":99.99,"active":true}""";
        mvc.perform(put("/api/v1/products/" + pid)
                        .header("Authorization", bearer(adminToken()))
                        .contentType(json)
                        .content(change))
                .andExpect(status().isOk());

        // verify order price unchanged
        mvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", bearer(token())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].unitPrice").value(10.00));
    }

    @Test
    void otherCustomersOrderReturns404() throws Exception {
        long pid = stockNewProduct("ORD-TST-P06", "10.00", 100);
        String order = mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token()))
                        .contentType(json).content(orderBody(pid, 1)))
                .andReturn().getResponse().getContentAsString();

        long orderId = ((Number) JsonPath.read(order, "$.id")).longValue();

        // try read order as another customer
        String other = registerCustomer("otherOrder@example.com");
        mvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", bearer(other)))
                .andExpect(status().isNotFound());

        // admin can read
        mvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk());
    }

    @Test
    void idempotentRetryWontReserveTwice() throws Exception {
        long pid = stockNewProduct("ORD-TST-P07", "10.00", 100);
        String key = "idem-key-qwe123";

        String first = mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token()))
                        .header("Idempotency-Key", key)
                        .contentType(json)
                        .content(orderBody(pid, 2)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long firstId = ((Number) JsonPath.read(first, "$.id")).longValue();

        String second = mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token()))
                        .header("Idempotency-Key", key)
                        .contentType(json)
                        .content(orderBody(pid, 2)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long secondId = ((Number) JsonPath.read(second, "$.id")).longValue();

        // verify same order
        assertThat(secondId).isEqualTo(firstId);

        // reserved only once
        mvc.perform(get("/api/v1/inventory/" + pid)
                        .header("Authorization", bearer(adminToken())))
                .andExpect(jsonPath("$.quantityReserved").value(2));
    }

    @Test
    void authRequiredToPlaceOrder() throws Exception {
        mvc.perform(post("/api/v1/orders")
                        .contentType(json)
                        .content(orderBody(1L, 1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void concurrentOrdersWontOversell() throws Exception {
        long pid = stockNewProduct("ORD-RACE-TST", "10.00", 1);
        String token = token();

        Callable<Integer> place = () -> mvc.perform(post("/api/v1/orders")
                        .header("Authorization", bearer(token))
                        .contentType(json).content(orderBody(pid, 1)))
                .andReturn().getResponse().getStatus();

        // simulate concurrency + store return status
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<Integer> a = pool.submit(place);
        Future<Integer> b = pool.submit(place);
        int statusA = a.get();
        int statusB = b.get();
        pool.shutdown();

        // verify 1 success && 1 reject
        assertThat(List.of(statusA, statusB)).containsExactlyInAnyOrder(201, 409);

        // verify avail == 0 && resv == 1
        mvc.perform(get("/api/v1/inventory/" + pid).header("Authorization", bearer(adminToken())))
                .andExpect(jsonPath("$.availableQuantity").value(0))
                .andExpect(jsonPath("$.quantityReserved").value(1));
    }
}




