package com.garrettw011.orderflow.inventory;

import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InventoryApiTest extends AbstractIntegrationTest {
    @Autowired
    InventoryRepository inventoryRepository;

    private long createProduct(String sku) throws Exception {
        String body = mvc.perform(post("/api/v1/products")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(appJson).content(productBody(sku, sku, "10.00")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private void adjust(long productId, int delta, String token) throws Exception {
        mvc.perform(patch("/api/v1/inventory/" + productId + "/adjust")
                        .header("Authorization", bearer(token))
                        .contentType(appJson).content("{\"delta\":" + delta + "}"))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanAdjustUp() throws Exception {
        long pid = createProduct("INV-UP-001");
        mvc.perform(patch("/api/v1/inventory/" + pid + "/adjust")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(appJson).content("{\"delta\":20}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityOnHand").value(20))
                .andExpect(jsonPath("$.availableQuantity").value(20));
    }

    @Test
    void managerCanAdjustDown() throws Exception {
        long pid = createProduct("INV-DWN-001");
        adjust(pid, 20, managerToken());
        mvc.perform(patch("/api/v1/inventory/" + pid + "/adjust")
                        .header("Authorization", bearer(managerToken()))
                        .contentType(appJson).content("{\"delta\":-5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantityOnHand").value(15));
    }

    @Test
    void adjustBelowZeroFails() throws Exception {
        long pid = createProduct("INV-NEG-001");
        mvc.perform(patch("/api/v1/inventory/" + pid + "/adjust")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(appJson).content("{\"delta\":-5}"))
                .andExpect(status().isConflict());
    }

    @Test
    void adjustBelowReservedFails() throws Exception {
        long pid = createProduct("INV-RSV-001");
        adjust(pid, 10, adminToken());

        InventoryItem item = inventoryRepository.findByProductId(pid).orElseThrow();
        item.setQuantityReserved(8);
        inventoryRepository.saveAndFlush(item);

        mvc.perform(patch("/api/v1/inventory/" + pid + "/adjust")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(appJson).content("{\"delta\":-5}"))
                .andExpect(status().isConflict());
    }

    @Test
    void getByProductIdReturnsAvailQty() throws Exception {
        long pid = createProduct("INV-GET-001");
        adjust(pid, 8, adminToken());
        mvc.perform(get("/api/v1/inventory/" + pid)
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("INV-GET-001"))
                .andExpect(jsonPath("$.quantityOnHand").value(8))
                .andExpect(jsonPath("$.availableQuantity").value(8));
    }

    @Test
    void lowStockIncludesItemsAtThreshold() throws Exception {
        String body = mvc.perform(get("/api/v1/inventory/low-stock")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<String> skus = JsonPath.read(body, "$[*].sku");
        assertThat(skus).contains("MXS-CHR-ERG-001");
    }

    @Test
    void customerAdjustFails() throws Exception {
        long pid = createProduct("INV-CUST-1");
        mvc.perform(patch("/api/v1/inventory/" + pid + "/adjust")
                        .header("Authorization", bearer(token()))
                        .contentType(appJson).content("{\"delta\":5}"))
                .andExpect(status().isForbidden());
    }

    @Test void staleUpdateTriggersOptimistLockFail() throws Exception {
        long pid = createProduct("INV-LOCK-001");

        InventoryItem a = inventoryRepository.findByProductId(pid).orElseThrow();
        InventoryItem b = inventoryRepository.findByProductId(pid).orElseThrow();

        a.setQuantityOnHand(5);
        inventoryRepository.saveAndFlush(a);

        b.setQuantityOnHand(9);
        assertThatThrownBy(() -> inventoryRepository.saveAndFlush(b))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }
}




