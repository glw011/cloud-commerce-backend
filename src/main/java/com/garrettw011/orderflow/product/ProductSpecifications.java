package com.garrettw011.orderflow.product;

import com.garrettw011.orderflow.product.dto.ProductSearchParams;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

public final class ProductSpecifications {
    private ProductSpecifications() {}

    private static Specification<Product> activeEquals(Boolean active) {
        return active == null
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.equal(root.get("active"), active);
    }

    private static Specification<Product> priceAtLeast(BigDecimal min) {
        return min == null
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    private static Specification<Product> priceAtMost(BigDecimal max) {
        return max == null
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }

    private static Specification<Product> nameContains(String name) {
        return (name == null || name.isBlank())
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<Product> skuEquals(String sku) {
        return (sku == null || sku.isBlank())
                ? Specification.unrestricted()
                : (root, query, cb) -> cb.equal(root.get("sku"), sku.trim().toUpperCase());
    }

    public static Specification<Product> withFilters(ProductSearchParams p) {
        return Specification.allOf(
                activeEquals(p.active()),
                priceAtLeast(p.minPrice()),
                priceAtMost(p.maxPrice()),
                nameContains(p.name()),
                skuEquals(p.sku()));
    }
}


