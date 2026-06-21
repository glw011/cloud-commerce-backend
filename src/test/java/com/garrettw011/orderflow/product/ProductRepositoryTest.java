package com.garrettw011.orderflow.product;

import com.garrettw011.orderflow.support.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = NONE)
class ProductRepositoryTest extends AbstractPostgresTest {

    @Autowired ProductRepository products;

    private Product product(String sku, BigDecimal price) {
        Product p = new Product();
        p.setSku(sku);
        p.setName("Test " + sku);
        p.setPrice(price);
        p.setActive(true);
        return p;
    }

    @Test
    void migrationsRanAndDemoDataIsPresent() {
        Optional<Product> demo = products.findBySku("KM-KB-MECH-001");
        assertThat(demo).isPresent();
        assertThat(demo.get().getPrice()).isEqualByComparingTo("89.99");
    }

    @Test
    void findBySkuReturnsSavedProduct() {
        products.saveAndFlush(product("TEST-001", new BigDecimal("12.50")));
        assertThat(products.findBySku("TEST-001")).isPresent();
    }

    @Test
    void duplicateSkuIsRejected() {
        products.saveAndFlush(product("DUP-001", new BigDecimal("9.99")));
        assertThatThrownBy(() -> products.saveAndFlush(product("DUP-001", new BigDecimal("9.99"))))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void negativePriceIsRejected() {
        assertThatThrownBy(() -> products.saveAndFlush(product("BAD-001", new BigDecimal("-13.99"))))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}









