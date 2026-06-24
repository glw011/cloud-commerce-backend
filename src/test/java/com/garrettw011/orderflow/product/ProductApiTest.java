package com.garrettw011.orderflow.product;

import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductApiTest extends AbstractIntegrationTest {

    // Public 'read' tests

    @Test
    void listReturnsPageResponseShape() throws Exception {
        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void getBySkuReturnsProduct() throws Exception {
        mvc.perform(get("/api/v1/products/sku/KM-KB-MECH-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("KM-KB-MECH-001"))
                .andExpect(jsonPath("$.price").value(89.99));
    }

    @Test
    void getByIdReturnsProduct() throws Exception {
        String body = mvc.perform(get("/api/v1/products/sku/3M-MS-WRLSS-001"))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(body, "$.id");

        mvc.perform(get("/api/v1/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("3M-MS-WRLSS-001"));
    }

    @Test
    void badIdReturns404() throws Exception {
        mvc.perform(get("/api/v1/products/171717171717"))
                .andExpect(status().isNotFound());
    }

    // Admin 'write' tests

    @Test
    void adminCanCreateProduct() throws Exception {
        mvc.perform(post("/api/v1/products")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(appJson)
                        .content(productBody("NEW-SKU-001", "New", "9.99")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("NEW-SKU-001"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void adminCanDeactivateProduct() throws Exception {
        String created = mvc.perform(post("/api/v1/products")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(appJson)
                        .content(productBody("DEAC-TIV-001", "Deactivate", "0.99")))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(created, "$.id");

        mvc.perform(patch("/api/v1/products/" + id + "/deactivate")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void duplicateSkuReturns409() throws Exception {
        mvc.perform(post("/api/v1/products")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(appJson)
                        .content(productBody("KM-KB-MECH-001", "Duplicate", "3.50")))
                .andExpect(status().isConflict());
    }

    @Test
    void badPriceReturns400() throws Exception {
        mvc.perform(post("/api/v1/products")
                        .header("Authorization", bearer(adminToken()))
                        .contentType(appJson)
                        .content(productBody("BAD-PRC-001", "Bad Price", "-0.99")))
                .andExpect(status().isBadRequest());
    }
}
























