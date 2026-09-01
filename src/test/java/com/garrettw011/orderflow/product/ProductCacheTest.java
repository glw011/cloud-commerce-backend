package com.garrettw011.orderflow.product;

import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductCacheTest extends AbstractIntegrationTest {
    @Autowired CacheManager cacheManager;
    @Autowired ProductRepository productRepository;

    @BeforeEach
    void clearCache() {
        cacheManager.getCache("products").clear();
        cacheManager.getCache("product-list").clear();
    }

    @Test
    void readsFromCache() throws Exception {
        long id = createProduct("CACHE-1", "10.00");
        mvc.perform(get("/api/v1/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test CACHE-1"));

        // update product's name in db (bypassing service so no evict triggers)
        Product p = productRepository.findById(id).orElseThrow();
        p.setName("CacheTestEdit1");
        productRepository.saveAndFlush(p);

        mvc.perform(get("/api/v1/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test CACHE-1"));
    }

    @Test
    void updateEvictsCachedProduct() throws Exception {
        long id = createProduct("CACHE-TST-2", "10.00");
        mvc.perform(get("/api/v1/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test CACHE-TST-2"));

        // evict product cache
        String body = """
                      {"name":"CacheTest2","price":10.00,"active":true}""";
        mvc.perform(put("/api/v1/products/" + id)
                        .header("Authorization", bearer(adminToken()))
                        .contentType(json)
                        .content(body))
                .andExpect(status().isOk());

        // verify cache was evicted
        assertThat(cacheManager.getCache("products").get("id:" + id)).isNull();

        mvc.perform(get("/api/v1/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CacheTest2"));
    }

    @Test
    void listCacheEvictsOnProductCreate() throws Exception {
        mvc.perform(get("/api/v1/products?sku=CACHE-LIST-TST-1")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        createProduct("CACHE-LIST-TST-1", "10.00");
        mvc.perform(get("/api/v1/products?sku=CACHE-LIST-TST-1")
                        .header("Authorization", bearer(adminToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}