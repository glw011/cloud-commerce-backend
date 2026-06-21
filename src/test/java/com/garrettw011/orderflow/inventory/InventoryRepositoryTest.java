package com.garrettw011.orderflow.inventory;

import com.garrettw011.orderflow.product.Product;
import com.garrettw011.orderflow.product.ProductRepository;
import com.garrettw011.orderflow.support.AbstractPostgresTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureTestDatabase(replace = NONE)
class InventoryRepositoryTest extends AbstractPostgresTest {
    @Autowired InventoryRepository inventory;
    @Autowired ProductRepository products;

    private Product newProduct() {
        Product p = new Product();
        p.setSku("INV-TST-" + System.nanoTime());
        p.setName("Inventory Test");
        p.setPrice(new BigDecimal("3.50"));
        p.setActive(true);
        return products.saveAndFlush(p);
    }

    @Test
    void findByProductIdWorks() {
        Product p = newProduct();
        InventoryItem item = new InventoryItem();
        item.setProduct(p);
        item.setQuantityOnHand(15);
        inventory.saveAndFlush(item);

        assertThat(inventory.findByProductId(p.getId())).isPresent();
    }

    @Test
    void negativeOnHandIsRejected() {
        Product p = newProduct();
        InventoryItem item = new InventoryItem();
        item.setProduct(p);
        item.setQuantityOnHand(-1);
        assertThatThrownBy(() -> inventory.saveAndFlush(item)).isInstanceOf(DataIntegrityViolationException.class);
    }
}